package de.rettichlp.therettingtoncompanion.gui.widgets.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.widgetService;
import static java.lang.Math.toIntExact;

public abstract class AbstractTRCTextWidget<C extends WidgetConfiguration> extends AbstractTRCWidget<C> {

    public abstract Component text();

    @Override
    public int getWidth() {
        int textWidth = this.minecraft.font.width(text());
        return toIntExact(toNearestScale(textWidth + 2 * TEXT_BOX_PADDING));
    }

    @Override
    public int getHeight() {
        int textHeight = this.minecraft.font.lineHeight;
        return toIntExact(toNearestScale(textHeight + 2 * TEXT_BOX_PADDING));
    }

    @Override
    public void extractWidget(@NotNull GuiGraphicsExtractor graphics, int x, int y, Color color, boolean backgroundEnabled) {
        if (backgroundEnabled) {
            Color transparentColor = widgetService.getTransparentColor(color);
            graphics.fill(x, y, x + getWidth(), y + getHeight(), transparentColor.getRGB());
        }

        graphics.text(this.minecraft.font, text(), x + getWidth() / 2 - this.minecraft.font.width(text()) / 2, y + getHeight() / 2 - this.minecraft.font.lineHeight / 2, color.getRGB(), false);
    }
}
