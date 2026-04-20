package de.rettichlp.therettingtoncompanion.common.gui.widgets;

import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.AbstractProgressTextWidget;
import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.Widget;
import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.WidgetConfiguration;
import lombok.RequiredArgsConstructor;
import net.minecraft.text.Text;

import java.awt.Color;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static net.minecraft.text.Text.empty;

@RequiredArgsConstructor
@Widget(registryName = "notification")
public class NotificationWidget extends AbstractProgressTextWidget<NotificationWidget.Configuration> {

    private final Text text;
    private final Color borderColor;
    private final LocalDateTime creationTime;
    private final long durationInMillis;

    @Override
    public Text text() {
        return this.text;
    }

    @Override
    public Color getBorderColor() {
        return this.borderColor;
    }

    @Override
    public double progress() {
        return calculateProgress(this.creationTime, this.durationInMillis);
    }

    @Override
    public Text getDisplayName() {
        return empty();
    }

    @Override
    public Text getTooltip() {
        return empty();
    }

    @Override
    public boolean isVisible() {
        return this.creationTime.plus(this.durationInMillis, MILLIS).isAfter(now());
    }

    public static class Configuration extends WidgetConfiguration {}
}
