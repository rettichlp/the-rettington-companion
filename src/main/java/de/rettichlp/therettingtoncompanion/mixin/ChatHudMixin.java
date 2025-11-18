package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.checkForHighlightedMessageAndRun;
import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.getString;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

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
        int highlightColor = ColorHelper.withAlpha(50, color.getRGB());

        checkForHighlightedMessageAndRun(getString(line.content()), () -> {
            // for some reason there is a 4px offset on the left side and 8px offset on the right side
            drawContext.fill(-4, y1, this.client.inGameHud.getChatHud().getWidth() + 8, y2, highlightColor);
        });
    }
}
