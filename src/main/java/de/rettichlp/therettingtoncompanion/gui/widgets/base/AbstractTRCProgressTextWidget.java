package de.rettichlp.therettingtoncompanion.gui.widgets.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.Temporal;

import static java.lang.Math.clamp;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;

public abstract class AbstractTRCProgressTextWidget<C extends WidgetConfiguration> extends AbstractTRCTextWidget<C> {

    @Override
    public abstract Component text();

    @Override
    public int getHeight() {
        return 4 * WIDGET_POSITION_SCALE;
    }

    @Override
    public void extractWidget(@NotNull GuiGraphicsExtractor graphics, int x, int y, Alignment alignment) {
        graphics.fill(x, y, x + getWidth(), y + getHeight(), getBackgroundColor().getRGB());
        graphics.text(this.minecraft.font, text(), x + getWidth() / 2 - this.minecraft.font.width(text()) / 2, y + getHeight() / 2 - this.minecraft.font.lineHeight / 2, 0xFFFFFFFF, false);

        int maxProgressWidth = getWidth() - TEXT_BOX_PADDING * 2;
        int xProgressStart = (int) (x + TEXT_BOX_PADDING + maxProgressWidth * progress());
        int xProgressEnd = x + getWidth() - TEXT_BOX_PADDING;

        graphics.horizontalLine(xProgressStart, xProgressEnd, y + getHeight() - 3, getBorderColor().getRGB());
    }

    public abstract double progress();

    protected double calculateProgress(Temporal creationTime, long durationInMillis) {
        long elapsedMillis = between(creationTime, now()).toMillis();
        double progress = (double) elapsedMillis / durationInMillis;
        return clamp(progress, 0.0, 1.0);
    }
}
