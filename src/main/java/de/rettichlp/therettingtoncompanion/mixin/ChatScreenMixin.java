package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.gui.ChatTabButton;
import de.rettichlp.therettingtoncompanion.gui.PatternEditBox;
import de.rettichlp.therettingtoncompanion.gui.screens.ChatTabPopupScreen;
import de.rettichlp.therettingtoncompanion.utils.ChatUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
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
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.CHAT_TAB_BUTTONS;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.FOCUSED_CHAT_TAB;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatBottomHeight;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatLeft;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatRight;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getGuiMessageBounds;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getHoveredGuiMessage;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getUnreadDividerY;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.isMessageVisible;
import static java.awt.Color.CYAN;
import static java.awt.Color.RED;
import static java.lang.Integer.MIN_VALUE;
import static java.util.regex.Pattern.compile;
import static net.minecraft.network.chat.Component.translatable;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Unique
    private @Nullable PatternEditBox patternEditBox;

    @Unique
    private @Nullable Button copyButton;

    @Unique
    private @Nullable Button copyButtonWithOutTimestamp;

    @Unique
    private @Nullable GuiMessage contextMenuMessage;

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractRenderState",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
                     shift = AFTER))
    public void trc$extractRenderStateInvoke(@NonNull GuiGraphicsExtractor graphics,
                                             int mouseX,
                                             int mouseY,
                                             float a,
                                             CallbackInfo ci) {
        if (this.contextMenuMessage != null) {
            ScreenRectangle guiMessageBounds = getGuiMessageBounds(this.contextMenuMessage, 9);
            graphics.fill(guiMessageBounds.left(), guiMessageBounds.top(), guiMessageBounds.right(), guiMessageBounds.top() + 1, CYAN.getRGB());
            graphics.fill(guiMessageBounds.left(), guiMessageBounds.bottom() - 1, guiMessageBounds.right(), guiMessageBounds.bottom(), CYAN.getRGB());
            graphics.fill(guiMessageBounds.left(), guiMessageBounds.top(), guiMessageBounds.left() + 1, guiMessageBounds.bottom(), CYAN.getRGB());
            graphics.fill(guiMessageBounds.right() - 1, guiMessageBounds.top(), guiMessageBounds.right(), guiMessageBounds.bottom(), CYAN.getRGB());
        }

        if (configuration.chat().isChatSearch() && this.patternEditBox != null) {
            graphics.fill(this.patternEditBox.getX() - 2, this.patternEditBox.getY() - 2, this.patternEditBox.getX() + this.patternEditBox.getWidth() - 2, this.patternEditBox.getY() + this.patternEditBox.getHeight() - 2, this.minecraft.options.getBackgroundColor(MIN_VALUE));
        }

        // marks where the messages that were unread when this tab got focused end, so newly arrived ones stay easy to spot
        Integer unreadDividerY = getUnreadDividerY();
        if (unreadDividerY != null) {
            graphics.fill(getChatLeft(), unreadDividerY, getChatRight(), unreadDividerY + 1, RED.getRGB());
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void trc$mouseClickedHead(@NonNull MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        double mouseX = event.x();
        double mouseY = event.y();

        // check and handle mouse over context menu
        if (this.copyButton != null && this.copyButton.isMouseOver(mouseX, mouseY)) {
            cir.setReturnValue(this.copyButton.mouseClicked(event, doubleClick));
            return;
        } else if (this.copyButtonWithOutTimestamp != null && this.copyButtonWithOutTimestamp.isMouseOver(mouseX, mouseY)) {
            cir.setReturnValue(this.copyButtonWithOutTimestamp.mouseClicked(event, doubleClick));
            return;
        }

        // dispatch clicks to the chat tab bar - it's built and rendered centrally in HudMixin, ChatScreenMixin only forwards mouse
        // events to it since only a Screen receives those
        for (ChatTabButton chatTabButton : CHAT_TAB_BUTTONS) {
            if (!chatTabButton.isMouseOver(mouseX, mouseY)) {
                continue;
            }

            if (event.button() == 1 && chatTabButton.getChatTab() != null) {
                closeContextMenu();
                this.minecraft.gui.setScreen(new ChatTabPopupScreen(this, chatTabButton.getChatTab()));
                cir.setReturnValue(true);
                return;
            }

            if (event.button() == 0) {
                cir.setReturnValue(chatTabButton.mouseClicked(event, doubleClick));
                return;
            }
        }

        // handle mouse over message
        GuiMessage hoveredGuiMessage = getHoveredGuiMessage(mouseX, mouseY);
        if (hoveredGuiMessage == null) {
            closeContextMenu();
            return;
        }

        if (event.button() == 1) {
            openContextMenu(hoveredGuiMessage, mouseX, mouseY);
            cir.setReturnValue(true);
        } else {
            closeContextMenu();
        }
    }

    @Inject(method = "removed", at = @At("HEAD"))
    public void trc$removedHead(CallbackInfo ci) {
        this.minecraft.gui.hud.getChat().setVisibleMessageFilter(ChatUtils::isMessageVisible);
        closeContextMenu();

        // leaving the chat screen counts as leaving the focused tab, so clear its unread state and divider line
        if (FOCUSED_CHAT_TAB != null) {
            FOCUSED_CHAT_TAB.setUnreadCount(0);
            FOCUSED_CHAT_TAB.setFilterTriggered(false);
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void trc$initTail(CallbackInfo ci) {
        this.copyButton = Button.builder(translatable("trc.chat_screen.context_menu.copy_message"), _ -> copyToClipboard(false))
                .size(150, 14)
                .build();
        this.copyButton.visible = false;
        addRenderableWidget(this.copyButton);

        this.copyButtonWithOutTimestamp = Button.builder(translatable("trc.chat_screen.context_menu.copy_message_without_timestamp"), _ -> copyToClipboard(true))
                .size(150, 14)
                .build();
        this.copyButtonWithOutTimestamp.visible = false;
        addRenderableWidget(this.copyButtonWithOutTimestamp);

        LocalPlayer player = this.minecraft.player;
        if (configuration.chat().isChatSearch() && player != null) {
            this.patternEditBox = new PatternEditBox(this.minecraft.font, "", this::onSearchChanged);
            this.patternEditBox.setSize(this.minecraft.getWindow().getGuiScaledWidth() / 2 - 91 - 7 - 2, 12); // right end of left offhand slot
            this.patternEditBox.setPosition(4, getChatBottomHeight() + 4);
            this.patternEditBox.setHint(translatable("debug.options.search"));
            this.patternEditBox.setMaxLength(256);
            this.patternEditBox.setBordered(false);
            this.patternEditBox.addFormatter((_, _) -> null);
            addRenderableWidget(this.patternEditBox);
        }

        updateVisibleMessageFilter();
    }

    @Unique
    private void onSearchChanged(String patternString) {
        ChatComponent chat = this.minecraft.gui.hud.getChat();
        chat.setVisibleMessageFilter(guiMessage -> isMessageVisible(guiMessage) && (patternString.isBlank() || compile(patternString).matcher(guiMessage.content().getString()).find()));
    }

    @Unique
    private void updateVisibleMessageFilter() {
        ChatComponent chat = this.minecraft.gui.hud.getChat();
        String searchPattern = this.patternEditBox != null ? this.patternEditBox.getValue() : "";
        chat.setVisibleMessageFilter(guiMessage -> isMessageVisible(guiMessage) && (searchPattern.isBlank() || compile(searchPattern).matcher(guiMessage.content().getString()).find()));
    }

    @Unique
    private void openContextMenu(GuiMessage message, double mouseX, double mouseY) {
        this.contextMenuMessage = message;

        if (this.copyButton != null && this.copyButtonWithOutTimestamp != null) {
            this.copyButton.setPosition((int) mouseX, (int) mouseY);
            this.copyButton.visible = true;
            this.copyButtonWithOutTimestamp.setPosition((int) mouseX, (int) mouseY + this.copyButton.getHeight() + 2);
            this.copyButtonWithOutTimestamp.visible = configuration.chat().isChatTime();
        }
    }

    @Unique
    private void closeContextMenu() {
        this.contextMenuMessage = null;

        if (this.copyButton != null && this.copyButtonWithOutTimestamp != null) {
            this.copyButton.visible = false;
            this.copyButtonWithOutTimestamp.visible = false;
        }
    }

    @Unique
    private void copyToClipboard(boolean hideTimestamp) {
        if (this.contextMenuMessage == null) {
            return;
        }

        String text = this.contextMenuMessage.content().getString();
        this.minecraft.keyboardHandler.setClipboard(hideTimestamp && text.matches("^\\d{2}:\\d{2}:\\d{2} .*") ? text.substring(9) : text);
        closeContextMenu();
    }
}
