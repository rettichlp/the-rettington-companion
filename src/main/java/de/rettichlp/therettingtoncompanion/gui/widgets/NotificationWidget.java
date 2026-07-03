package de.rettichlp.therettingtoncompanion.gui.widgets;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCProgressTextWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import net.minecraft.network.chat.Component;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static net.minecraft.network.chat.Component.translatable;

public class NotificationWidget extends AbstractTRCProgressTextWidget<NotificationWidget.Configuration> {

    private final Component text;
    private final LocalDateTime creationTime;
    private final long durationInMillis;

    @Override
    public String getRegistryName() {
        return "notification";
    }

    @Override
    public Component getLabel() {
        return translatable("trc.widgets.notification.label");
    }

    @Override
    public Component getTooltip() {
        return translatable("trc.widgets.notification.tooltip");
    }

    @Override
    public void addOptions(TRCOptionsList optionsList) {}

    @Override
    public boolean isVisible() {
        return this.creationTime.plus(this.durationInMillis, MILLIS).isAfter(now());
    }

    @Override
    public Component text() {
        return this.text;
    }

    @Override
    public double progress() {
        return calculateProgress(this.creationTime, this.durationInMillis);
    }

    public static class Configuration extends WidgetConfiguration {}
}
