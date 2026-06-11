package de.rettichlp.therettingtoncompanion.common.gui.widgets.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.temporal.Temporal;

import static de.rettichlp.therettingtoncompanion.common.services.RenderService.TEXT_BOX_PADDING;
import static java.lang.Math.clamp;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;

public abstract class AbstractProgressTextWidget<C extends WidgetConfiguration> extends AbstractTextWidget<C> {

    @Override
    public void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y, Alignment alignment) {
        graphics.fill(x, y, x + getWidth(), y + getHeight(), getBackgroundColor().getRGB());
        graphics.text(getFont(), text(), x + TEXT_BOX_PADDING, y + TEXT_BOX_PADDING, 0xFFFFFFFF, false);

        int maxProgressWidth = getWidth() - TEXT_BOX_PADDING * 2;
        int xProgressStart = (int) (x + TEXT_BOX_PADDING + maxProgressWidth * progress());
        int xProgressEnd = x + getWidth() - TEXT_BOX_PADDING;

        graphics.horizontalLine(xProgressStart, xProgressEnd, y + getHeight() - 3, getColor().getRGB());
    }

    public abstract double progress();

    protected double calculateProgress(Temporal creationTime, @NonNull Duration duration) {
        Duration elapsed = between(creationTime, now());
        double progress = (double) elapsed.toMillis() / duration.toMillis();
        return clamp(progress, 0.0, 1.0);
    }
}
