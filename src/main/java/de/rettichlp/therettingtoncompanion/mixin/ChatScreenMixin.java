package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.gui.PatternEditBox;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatBottomHeight;
import static java.lang.Integer.MIN_VALUE;
import static java.util.regex.Pattern.compile;
import static net.minecraft.network.chat.Component.translatable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Unique
    private final PatternEditBox patternEditBox = new PatternEditBox(this.minecraft.font, "", this::onSearchChanged);

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

    @Inject(method = "init", at = @At("TAIL"))
    protected void trc$initTail(CallbackInfo ci) {
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
    private void trc$removedHead(CallbackInfo ci) {
        this.minecraft.gui.hud.getChat().setVisibleMessageFilter(_ -> true);
    }

    @Unique
    private void onSearchChanged(String patternString) {
        ChatComponent chat = this.minecraft.gui.hud.getChat();
        chat.setVisibleMessageFilter(guiMessage -> patternString.isBlank() || compile(patternString).matcher(guiMessage.content().getString()).find());
    }
}
