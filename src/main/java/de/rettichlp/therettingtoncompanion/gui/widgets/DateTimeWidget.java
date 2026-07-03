package de.rettichlp.therettingtoncompanion.gui.widgets;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCTextWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.format.DateTimeFormatter;

import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

public class DateTimeWidget extends AbstractTRCTextWidget<DateTimeWidget.Configuration> {

    public static final DateTimeFormatter DATE_TIME_FORMAT = ofPattern("dd.MM.yyyy HH:mm:ss");
    public static final DateTimeFormatter TIME_FORMAT = ofPattern("HH:mm:ss");

    @Override
    public @Nullable String getRegistryName() {
        return "date_time";
    }

    @Override
    public Component getLabel() {
        return translatable("trc.widgets.date_time.label");
    }

    @Override
    public Component getTooltip() {
        return translatable("trc.widgets.date_time.tooltip");
    }

    @Override
    public void addOptions(@NonNull TRCOptionsList optionsList) {
        optionsList.addToggleButton(translatable("trc.widgets.date_time.options.show_date.label"), create(translatable("trc.widgets.date_time.options.show_date.tooltip")), true, (_, value) -> getWidgetConfiguration().setShowDate(value == ON));
    }

    @Override
    public Component text() {
        DateTimeFormatter formatter = getWidgetConfiguration().isShowDate() ? DATE_TIME_FORMAT : TIME_FORMAT;
        return literal(now().format(formatter));
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Configuration extends WidgetConfiguration {

        private boolean showDate = false;
    }
}
