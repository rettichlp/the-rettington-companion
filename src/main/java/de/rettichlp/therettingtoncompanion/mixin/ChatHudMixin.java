package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.getHighlightColor;
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

    @Redirect(method = "method_71992",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0))
    public void trc$method_71992Invoke(DrawContext drawContext,
                                       int x1,
                                       int y1,
                                       int x2,
                                       int y2,
                                       int color,
                                       @Local(argsOnly = true) ChatHudLine.Visible line) {
        int backgroundColor = color;

        Formatting highlightColor = getHighlightColor(getString(line.content()));
        if (highlightColor != null && highlightColor.getColorValue() != null) {
            backgroundColor = withAlpha(100, 0xFF000000 | highlightColor.getColorValue());
        }

        drawContext.fill(x1, y1, x2, y2, backgroundColor);
    }

    @ModifyReturnValue(method = "getWidth()I", at = @At("RETURN"))
    private int trc$getWidthReturn(int width) {
        if (!configuration.chat().isOptimizedChatSize()) {
            return width;
        }

        // from x = 0 to hotbar (length = 182)
        int chatWidth = this.client.getWindow().getScaledWidth() / 2 - 91 - 12; // for some reason there is a 12px offset
        double minecraftChatWidth = getWidth(this.client.options.getChatWidth().getValue());

        return (int) max(chatWidth, minecraftChatWidth);
    }

    @ModifyReturnValue(method = "getHeight()I", at = @At("RETURN"))
    private int trc$getHeightReturn(int height) {
        if (!configuration.chat().isOptimizedChatSize()) {
            return height;
        }

        // half of the screen height
        int chatHeight = this.client.getWindow().getScaledHeight() / 2;
        double minecraftChatHeight = getHeight(this.client.options.getChatHeightFocused().getValue());

        return isChatFocused() ? ((int) max(chatHeight, minecraftChatHeight)) : height;
    }
}
