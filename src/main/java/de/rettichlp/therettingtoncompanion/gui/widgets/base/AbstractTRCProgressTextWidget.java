package de.rettichlp.therettingtoncompanion.gui.widgets.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static java.lang.Math.toIntExact;

public abstract class AbstractTRCProgressTextWidget<C extends WidgetConfiguration> extends AbstractTRCTextWidget<C> {

    public abstract double progress();

    @Override
    public int getHeight() {
        int textHeight = this.minecraft.font.lineHeight;
        return toIntExact(toNearestScale(textHeight + 3 * configuration.widgets().getPadding()));
    }

    @Override
    public void extractWidget(@NotNull GuiGraphicsExtractor graphics, int x, int y, Color color, boolean backgroundEnabled) {
        super.extractWidget(graphics, x, y, color, backgroundEnabled);

        int padding = configuration.widgets().getPadding();
        int maxProgressWidth = getWidth() - padding * 2;
        int xProgressStart = (int) (x + padding + maxProgressWidth * progress());
        int xProgressEnd = x + getWidth() - padding;

        graphics.horizontalLine(xProgressStart, xProgressEnd, y + getHeight() - 3, color.getRGB());
    }
}
