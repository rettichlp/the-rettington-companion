package de.rettichlp.therettingtoncompanion.services;

import com.mojang.blaze3d.platform.NativeImage;
import de.rettichlp.therettingtoncompanion.mixin.OverlayTextureAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;

public class VisualsService {

    public int getDamageOverlayColor() {
        int rgb = configuration.visuals().getDamageOverlayColor() & 0x00FFFFFF; // isolate RGB
        int opacityPercent = configuration.visuals().getDamageOverlayOpacity(); // 0 - 100
        // Minecraft's entity shader blends this texture as mix(overlayColor, originalColor, alpha), so alpha is inverted compared to
        // normal transparency: 0 = full overlay color, 255 = original color (no tint)
        int alpha = (int) ((100 - opacityPercent) / 100.0F * 255.0F);
        return (alpha << 24) | rgb;
    }

    public void refreshDamageOverlayColor() {
        // OverlayTexture is built once when GameRenderer is created, so a config change made in-game has to repaint and re-upload the
        // already existing texture to take effect immediately, otherwise the new color only applies after a game restart
        DynamicTexture texture = ((OverlayTextureAccessor) Minecraft.getInstance().gameRenderer.overlayTexture()).getTexture();
        int color = getDamageOverlayColor();
        NativeImage pixels = texture.getPixels();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 16; x++) {
                pixels.setPixel(x, y, color);
            }
        }

        texture.upload();
    }
}
