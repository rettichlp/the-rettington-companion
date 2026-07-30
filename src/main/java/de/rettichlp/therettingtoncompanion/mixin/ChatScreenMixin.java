package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.gui.PatternEditBox;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.LAST_HOVERED_GUI_MESSAGE;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatBottomHeight;
import static java.lang.Integer.MIN_VALUE;
import static java.util.regex.Pattern.compile;
import static net.minecraft.network.chat.Component.translatable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Unique
    private final PatternEditBox patternEditBox = new PatternEditBox(this.minecraft.font, "", this::onSearchChanged);

    @Unique
    private @Nullable Button copyButton;

    @Unique
    private @Nullable Button copyButtonWithOutTimestamp;

    @Unique
    private @Nullable GuiMessage selectedMessage;

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!configuration.chat().isOptimizedChat()) {
            return;
        }

        graphics.fill(this.patternEditBox.getX() - 2, this.patternEditBox.getY() - 2, this.patternEditBox.getX() + this.patternEditBox.getWidth() - 2, this.patternEditBox.getY() + this.patternEditBox.getHeight() - 2, this.minecraft.options.getBackgroundColor(MIN_VALUE));
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void trc$mouseClickedHead(@NonNull MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (HOVERED_GUI_MESSAGE == null) {
            return;
        }

        double mouseX = event.x();
        double mouseY = event.y();

        if (this.selectedMessage == null) { // context menu closed
            if (event.button() == 1) {
                openContextMenu(HOVERED_GUI_MESSAGE, mouseX, mouseY);
            }
        } else { // context menu open
            if (this.copyButton != null && this.copyButton.isMouseOver(mouseX, mouseY)) {
                cir.setReturnValue(this.copyButton.mouseClicked(event, doubleClick));
            } else if (this.copyButtonWithOutTimestamp != null && this.copyButtonWithOutTimestamp.isMouseOver(mouseX, mouseY)) {
                cir.setReturnValue(this.copyButtonWithOutTimestamp.mouseClicked(event, doubleClick));
            } else {
                closeContextMenu();
            }
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void trc$initTail(CallbackInfo ci) {
        this.copyButton = Button.builder(translatable("trc.chat_screen.context_menu.copy_message"), _ -> copyToClipboard(false))
                .size(150, 14)
                .build();

        addRenderableWidget(this.copyButton);

        this.copyButtonWithOutTimestamp = Button.builder(translatable("trc.chat_screen.context_menu.copy_message_without_timestamp"), _ -> copyToClipboard(true))
                .size(150, 14)
                .build();
        addRenderableWidget(this.copyButtonWithOutTimestamp);

        LocalPlayer player = this.minecraft.player;
        if (configuration.chat().isOptimizedChat() && player != null) {
            this.patternEditBox.setSize(this.minecraft.getWindow().getGuiScaledWidth() / 2 - 91 - 7 - 2, 12); // right end of left offhand slot
            this.patternEditBox.setPosition(4, getChatBottomHeight(this.minecraft, player) + 4);
            this.patternEditBox.setHint(translatable("debug.options.search"));
            this.patternEditBox.setMaxLength(256);
            this.patternEditBox.setBordered(false);
            this.patternEditBox.addFormatter((_, _) -> null);
            addRenderableWidget(this.patternEditBox);
        }
    }

    @Inject(method = "removed", at = @At("HEAD"))
    public void trc$removedHead(CallbackInfo ci) {
        this.minecraft.gui.hud.getChat().setVisibleMessageFilter(_ -> true);
        closeContextMenu();
    }

    @Unique
    private void onSearchChanged(String patternString) {
        ChatComponent chat = this.minecraft.gui.hud.getChat();
        chat.setVisibleMessageFilter(guiMessage -> patternString.isBlank() || compile(patternString).matcher(guiMessage.content().getString()).find());
    }

    @Unique
    private void openContextMenu(GuiMessage message, double mouseX, double mouseY) {
        this.selectedMessage = message;

        if (this.copyButton != null && this.copyButtonWithOutTimestamp != null) {
            this.copyButton.setPosition((int) mouseX, (int) mouseY);
            this.copyButton.visible = true;
            this.copyButtonWithOutTimestamp.setPosition((int) mouseX, (int) mouseY + this.copyButton.getHeight() + 2);
            this.copyButtonWithOutTimestamp.visible = configuration.chat().isChatTime();
        }
    }

    @Unique
    private void closeContextMenu() {
        this.selectedMessage = null;

        if (this.copyButton != null && this.copyButtonWithOutTimestamp != null) {
            this.copyButton.visible = false;
            this.copyButtonWithOutTimestamp.visible = false;
        }
    }

    @Unique
    private void copyToClipboard(boolean hideTimestamp) {
        if (this.selectedMessage == null) {
            return;
        }

        String text = this.selectedMessage.content().getString();
        this.minecraft.keyboardHandler.setClipboard(hideTimestamp && text.matches("^\\d{2}:\\d{2}:\\d{2} ") ? text.substring(9) : text);
        closeContextMenu();
    }
}
