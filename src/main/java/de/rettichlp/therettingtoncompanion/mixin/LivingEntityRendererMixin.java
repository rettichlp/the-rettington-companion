package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

    @Definition(id = "livingEntity", local = @Local(type = LivingEntity.class, argsOnly = true))
    @Definition(id = "localPlayer", local = @Local(type = LocalPlayer.class))
    @Expression("livingEntity != localPlayer")
    @ModifyExpressionValue(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean trc$shouldShowNameExpression(boolean original) {
        return configuration.visuals().isShowOwnNametag() || original;
    }
}
