package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.common.models.GammaPreset.OWN_SETTING;
import static java.util.Optional.ofNullable;

@Mixin(OptionInstance.class)
public class SimpleOptionMixin<T> {

    @Unique
    private static final String GAMMA_OPTION_KEY = "options.gamma";

    @Shadow
    @Final
    private Component caption;

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void trc$getValueHead(CallbackInfoReturnable<Double> cir) {
        if (isGammaOption()) {
            cir.setReturnValue(ofNullable(configuration.getGammaPreset()).orElse(OWN_SETTING).getGammaValue());
        }
    }

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    public void trc$setValueHead(T value, CallbackInfo ci) {
        if (isGammaOption()) {
            configuration.setOwnGammaValue((Double) value);
            ci.cancel();
        }
    }

    @Unique
    private boolean isGammaOption() {
        if (this.caption instanceof TranslatableContents translatableContents) {
            return GAMMA_OPTION_KEY.equals(translatableContents.getKey());
        }

        return false;
    }
}
