package de.rettichlp.therettingtoncompanion.gui.widgets.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static java.lang.Math.toIntExact;

public abstract class AbstractTRCProgressTextWidget<C extends WidgetConfiguration> extends AbstractTRCTextWidget<C> {

    public abstract double progress();

    @Override
    public int getHeight() {
        int textHeight = this.minecraft.font.lineHeight;
        return toIntExact(toNearestScale(textHeight + 3 * TEXT_BOX_PADDING));
    }

    @Override
    public void extractWidget(@NotNull GuiGraphicsExtractor graphics, int x, int y, Color color, boolean backgroundEnabled) {
        super.extractWidget(graphics, x, y, color, backgroundEnabled);

        int maxProgressWidth = getWidth() - TEXT_BOX_PADDING * 2;
        int xProgressStart = (int) (x + TEXT_BOX_PADDING + maxProgressWidth * progress());
        int xProgressEnd = x + getWidth() - TEXT_BOX_PADDING;

        graphics.horizontalLine(xProgressStart, xProgressEnd, y + getHeight() - 3, color.getRGB());
    }
}
