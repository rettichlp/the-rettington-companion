package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.models.GammaPreset.FULLBRIGHT_NIGHT_VISION;
import static java.lang.Integer.MAX_VALUE;
import static net.minecraft.world.effect.MobEffects.NIGHT_VISION;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    private void trc$hasEffectHead(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if (configuration.visuals().getGammaPreset() == FULLBRIGHT_NIGHT_VISION) {
            cir.setReturnValue(effect.equals(NIGHT_VISION));
        }
    }

    @Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
    private void trc$getEffectHead(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (effect.equals(NIGHT_VISION) && configuration.visuals().getGammaPreset() == FULLBRIGHT_NIGHT_VISION) {
            cir.setReturnValue(new MobEffectInstance(NIGHT_VISION, MAX_VALUE, 0, false, false));
        }
    }
}
