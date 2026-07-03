package de.rettichlp.therettingtoncompanion.gui.widgets;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCProgressTextWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

import static de.rettichlp.therettingtoncompanion.services.WidgetService.calculateProgress;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static net.minecraft.network.chat.Component.translatable;

@RequiredArgsConstructor
public class CountdownWidget extends AbstractTRCProgressTextWidget<CountdownWidget.Configuration> {

    private final Component text;
    private final LocalDateTime creationTime;
    private final long durationInMillis;

    @Override
    public @Nullable String getRegistryName() {
        return "countdown";
    }

    @Override
    public Component text() {
        return this.text;
    }

    @Override
    public double progress() {
        return calculateProgress(this.creationTime, this.durationInMillis);
    }

    @Override
    public Component getLabel() {
        return translatable("trc.widgets.countdown.label");
    }

    @Override
    public Component getTooltip() {
        return translatable("trc.widgets.countdown.tooltip");
    }

    @Override
    public void addOptions(@NonNull TRCOptionsList optionsList) {}

    @Override
    public boolean isVisible() {
        return this.creationTime.plus(this.durationInMillis, MILLIS).isAfter(now());
    }

    public static class Configuration extends WidgetConfiguration {}
}
