package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.models.GammaPreset;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.models.GammaPreset.FULLBRIGHT_NIGHT_VISION;
import static de.rettichlp.therettingtoncompanion.models.GammaPreset.OWN_SETTING;

@Mixin(OptionInstance.class)
public class OptionInstanceMixin {

    @Shadow
    @Final
    private Component caption;

    @Inject(method = "get", at = @At("RETURN"), cancellable = true)
    private void trc$getHead(CallbackInfoReturnable<Double> cir) {
        if (!isGammaOption()) {
            return;
        }

        GammaPreset gammaPreset = configuration.getGammaPreset();

        if (gammaPreset != OWN_SETTING && gammaPreset != FULLBRIGHT_NIGHT_VISION) {
            cir.setReturnValue(gammaPreset.getGammaValue());
        }
    }

    @Unique
    private boolean isGammaOption() {
        return this.caption.getContents() instanceof TranslatableContents translatableContents && translatableContents.getKey().equals("options.gamma");
    }
}
