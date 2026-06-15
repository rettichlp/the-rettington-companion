package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.models.GammaPreset;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.models.GammaPreset.FULLBRIGHT_NIGHT_VISION;
import static de.rettichlp.therettingtoncompanion.models.GammaPreset.OWN_SETTING;

@Mixin(LightmapRenderStateExtractor.class)
public class LightmapRenderStateExtractorMixin {

    @ModifyVariable(method = "extract", at = @At("STORE"), name = "brightnessOption")
    private float trc$extractStore(float brightnessOption) {
        GammaPreset gammaPreset = configuration.getGammaPreset();

        if (gammaPreset == OWN_SETTING || gammaPreset == FULLBRIGHT_NIGHT_VISION) {
            return brightnessOption;
        }

        return (float) gammaPreset.getGammaValue();
    }
}
