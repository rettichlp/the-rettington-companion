package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.common.models.GammaPreset.FULLBRIGHT;
import static java.lang.Integer.MAX_VALUE;
import static net.minecraft.entity.effect.StatusEffects.NIGHT_VISION;

@Mixin(LivingEntity.class)
public class LightmapTextureManagerMixin {

    @Inject(method = "hasStatusEffect", at = @At("HEAD"), cancellable = true)
    private void trc$hasStatusEffectHead(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if (configuration.getGammaPreset() == FULLBRIGHT) {
            cir.setReturnValue(effect.equals(NIGHT_VISION));
        }
    }

    @Inject(method = "getStatusEffect", at = @At("HEAD"), cancellable = true)
    private void trc$getStatusEffectHead(@NotNull RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<StatusEffectInstance> cir) {
        if (effect.equals(NIGHT_VISION) && configuration.getGammaPreset() == FULLBRIGHT) {
            cir.setReturnValue(new StatusEffectInstance(NIGHT_VISION, MAX_VALUE, 0, false, false));
        }
    }
}
