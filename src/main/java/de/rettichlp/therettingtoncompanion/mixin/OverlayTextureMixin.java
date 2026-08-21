package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.visualsService;

@Mixin(OverlayTexture.class)
public abstract class OverlayTextureMixin {

    @ModifyArg(method = "<init>",
               at = @At(value = "INVOKE",
                        target = "Lcom/mojang/blaze3d/platform/NativeImage;setPixel(III)V",
                        ordinal = 0),
               index = 2)
    private int trc$initInvoke(int color) {
        return visualsService.getDamageOverlayColor();
    }
}
