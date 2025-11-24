package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
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

@Mixin(SimpleOption.class)
public class SimpleOptionMixin<T> {

    @Unique
    private static final String GAMMA_OPTION_KEY = "options.gamma";

    @Shadow
    @Final
    Text text;

    @Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
    public void getModifiedValue(CallbackInfoReturnable<Double> ci) {
        if (isGammaOption()) {
            ci.setReturnValue(ofNullable(configuration.getGammaPreset()).orElse(OWN_SETTING).getGammaValue());
        }
    }

    @Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
    public void setModValue(T value, CallbackInfo ci) {
        if (isGammaOption()) {
            configuration.setOwnGammaValue((Double) value);
            ci.cancel();
        }
    }

    @Unique
    private boolean isGammaOption() {
        if (this.text.getContent() instanceof TranslatableTextContent translatableTextContent) {
            return GAMMA_OPTION_KEY.equals(translatableTextContent.getKey());
        }

        return false;
    }
}
