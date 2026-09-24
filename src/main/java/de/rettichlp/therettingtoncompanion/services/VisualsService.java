package de.rettichlp.therettingtoncompanion.services;

import com.mojang.blaze3d.platform.NativeImage;
import de.rettichlp.therettingtoncompanion.mixin.OverlayTextureAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.CUSTOM_CROSSHAIR_SIZE;
import static net.minecraft.network.chat.TextColor.AQUA;
import static net.minecraft.network.chat.TextColor.BLACK;
import static net.minecraft.network.chat.TextColor.BLUE;
import static net.minecraft.network.chat.TextColor.DARK_AQUA;
import static net.minecraft.network.chat.TextColor.DARK_BLUE;
import static net.minecraft.network.chat.TextColor.DARK_GRAY;
import static net.minecraft.network.chat.TextColor.DARK_GREEN;
import static net.minecraft.network.chat.TextColor.DARK_PURPLE;
import static net.minecraft.network.chat.TextColor.DARK_RED;
import static net.minecraft.network.chat.TextColor.GOLD;
import static net.minecraft.network.chat.TextColor.GRAY;
import static net.minecraft.network.chat.TextColor.GREEN;
import static net.minecraft.network.chat.TextColor.LIGHT_PURPLE;
import static net.minecraft.network.chat.TextColor.RED;
import static net.minecraft.network.chat.TextColor.WHITE;
import static net.minecraft.network.chat.TextColor.YELLOW;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class VisualsService {

    public static final Collection<TextColor> VANILLA_TEXT_COLORS = List.of(BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE);
    public static final Identifier CUSTOM_CROSSHAIR_TEXTURE = fromNamespaceAndPath(MOD_ID, "dynamic/custom_crosshair");

    private @Nullable DynamicTexture customCrosshairTexture;

    public Identifier getCustomCrosshairTextureId() {
        if (this.customCrosshairTexture == null) {
            refreshCustomCrosshairTexture();
        }

        return CUSTOM_CROSSHAIR_TEXTURE;
    }

    public void refreshCustomCrosshairTexture() {
        int[] pixels = configuration.visuals().getCustomCrosshairPixels();

        if (this.customCrosshairTexture == null) {
            this.customCrosshairTexture = new DynamicTexture(() -> "custom_crosshair", new NativeImage(CUSTOM_CROSSHAIR_SIZE, CUSTOM_CROSSHAIR_SIZE, true));
            Minecraft.getInstance().getTextureManager().register(CUSTOM_CROSSHAIR_TEXTURE, this.customCrosshairTexture);
        }

        NativeImage image = this.customCrosshairTexture.getPixels();
        for (int y = 0; y < CUSTOM_CROSSHAIR_SIZE; y++) {
            for (int x = 0; x < CUSTOM_CROSSHAIR_SIZE; x++) {
                image.setPixel(x, y, pixels[y * CUSTOM_CROSSHAIR_SIZE + x]);
            }
        }

        this.customCrosshairTexture.upload();
    }

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
