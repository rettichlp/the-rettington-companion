package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;

@Mixin(NameTagFeatureRenderer.class)
public class NametagFeatureRendererMixin {

    @ModifyArg(method = "prepareText",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/Font;prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;"),
               index = 4)
    private static boolean trc$prepareText(boolean drawShadow) {
        return configuration.visuals().isNametagTextShadow();
    }
}
