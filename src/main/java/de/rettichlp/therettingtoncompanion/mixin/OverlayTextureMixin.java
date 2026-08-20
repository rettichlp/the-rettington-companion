package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;

@Mixin(OverlayTexture.class)
public abstract class OverlayTextureMixin {

    @ModifyArg(method = "<init>",
               at = @At(value = "INVOKE",
                        target = "Lcom/mojang/blaze3d/platform/NativeImage;setPixel(III)V",
                        ordinal = 0),
               index = 2)
    private int trc$initInvoke(int color) {
        int rgb = configuration.visuals().getDamageOverlayColor() & 0x00FFFFFF; // isolate RGB
        int opacityPercent = configuration.visuals().getDamageOverlayOpacity(); // 0 - 100
        int alpha = (int) (opacityPercent / 100.0F * 255.0F); // convert to 0-255
        return (alpha << 24) | rgb;
    }
}
