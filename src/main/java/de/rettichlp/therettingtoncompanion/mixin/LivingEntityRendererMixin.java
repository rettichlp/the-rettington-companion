package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @ModifyExpressionValue(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
    private Entity trc$shouldShowName(Entity original) {
        if (original != Minecraft.getInstance().player) {
            return original;
        }
        return configuration.visuals().isShowOwnNametag() ? null : original;
    }
}
