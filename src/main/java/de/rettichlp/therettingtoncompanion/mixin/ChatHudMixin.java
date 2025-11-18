package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.checkForHighlightedMessageAndRun;
import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.getString;
import static java.lang.Math.max;
import static net.minecraft.client.gui.hud.ChatHud.getHeight;
import static net.minecraft.client.gui.hud.ChatHud.getWidth;
import static net.minecraft.util.math.ColorHelper.withAlpha;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract boolean isChatFocused();

    @Inject(method = "method_71992",
            at = @At(value = "INVOKE",
                     shift = At.Shift.AFTER,
                     target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V",
                     ordinal = 0))
    public void highlight(DrawContext drawContext,
                          int i,
                          float f,
                          float g,
                          int j,
                          int k,
                          int x1,
                          int y1,
                          int y2,
                          ChatHudLine.Visible line,
                          int messageIndex,
                          float backgroundOpacity,
                          CallbackInfo ci) {
        Color color = new Color(190, 255, 0);
        int highlightColor = withAlpha(50, color.getRGB());

        checkForHighlightedMessageAndRun(getString(line.content()), () -> {
            // for some reason there is a 4px offset on the left side and 8px offset on the right side
            drawContext.fill(-4, y1, this.client.inGameHud.getChatHud().getWidth() + 8, y2, highlightColor);
        });
    }

    @ModifyReturnValue(method = "getWidth()I", at = @At("RETURN"))
    private int moreWidth(int width) {
        // from x = 0 to hotbar (length = 182)
        int chatWidth = this.client.getWindow().getScaledWidth() / 2 - 91 - 12; // for some reason there is a 12px offset
        double minecraftChatWidth = getWidth(this.client.options.getChatWidth().getValue());

        return (int) max(chatWidth, minecraftChatWidth);
    }

    @ModifyReturnValue(method = "getHeight()I", at = @At("RETURN"))
    private int moreFocusedHeight(int height) {
        // half of the screen height
        int chatHeight = this.client.getWindow().getScaledHeight() / 2;
        double minecraftChatHeight = getHeight(this.client.options.getChatHeightFocused().getValue());

        return isChatFocused() ? ((int) max(chatHeight, minecraftChatHeight)) : height;
    }
}
