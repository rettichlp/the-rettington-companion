package de.rettichlp.therettingtoncompanion.services;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class RenderService {

    public boolean isDebugEnabled() {
        return false;
    }

    public void renderShadowText(@NonNull GuiGraphicsExtractor graphics, Component text, int y, int color, int shadowColor) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);
        renderShadowText(graphics, text, (graphics.guiWidth() - textWidth) / 2, y, color, shadowColor);
    }

    private void renderShadowText(@NonNull GuiGraphicsExtractor graphics, Component text, int x, int y, int color, int shadowColor) {
        Font font = Minecraft.getInstance().font;

        // render shadow
        graphics.text(font, text, x + 1, y, shadowColor, false);
        graphics.text(font, text, x - 1, y, shadowColor, false);
        graphics.text(font, text, x, y + 1, shadowColor, false);
        graphics.text(font, text, x, y - 1, shadowColor, false);

        // render text
        graphics.text(font, text, x, y, color, false);
    }
}
