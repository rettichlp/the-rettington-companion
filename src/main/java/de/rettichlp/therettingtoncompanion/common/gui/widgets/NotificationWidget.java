package de.rettichlp.therettingtoncompanion.common.gui.widgets;

import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.AbstractProgressTextWidget;
import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.Widget;
import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.WidgetConfiguration;
import de.rettichlp.therettingtoncompanion.common.models.Notification;
import lombok.RequiredArgsConstructor;
import net.minecraft.text.Text;

import java.awt.Color;

import static net.minecraft.text.Text.empty;

@RequiredArgsConstructor
@Widget(registryName = "notification")
public class NotificationWidget extends AbstractProgressTextWidget<NotificationWidget.Configuration> {

    private final Notification notification;

    @Override
    public Text text() {
        return this.notification.getText();
    }

    @Override
    public Color getColor() {
        return this.notification.getColor();
    }

    @Override
    public double progress() {
        return calculateProgress(this.notification.getTimestamp(), this.notification.getDisplayDuration());
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
        return this.notification.isVisible();
    }

    public static class Configuration extends WidgetConfiguration {}
}
