package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.list.FullWidthButtonEntry;
import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.screens.ColorSelectionPopupScreen;
import de.rettichlp.therettingtoncompanion.gui.screens.WidgetPositionScreen;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.widgetService;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.translatable;

public class WidgetsOptionsTab extends AbstractTRCOptionsTab {

    public WidgetsOptionsTab() {
        super("widgets");
    }

    @Override
    public Component title() {
        return translatable("trc.option.widgets.title");
    }

    @Override
    public void populateOptionsList(@NonNull TRCOptionsList optionsList) {
        FullWidthButtonEntry fullWidthButtonEntry = optionsList.addFullWidthButton(translatable("trc.option.widgets.position.label"), create(translatable("trc.option.widgets.position.tooltip")), _ -> this.minecraft.setScreen(new WidgetPositionScreen()));
        fullWidthButtonEntry.getButton().active = this.minecraft.level != null;

        widgetService.getWidgets().forEach(abstractTRCWidget -> {
            Component label = abstractTRCWidget.getLabel();
            optionsList.addHeader(label);
            WidgetConfiguration widgetConfiguration = abstractTRCWidget.getWidgetConfiguration();
            optionsList.addToggleButton(translatable("trc.widgets.options.enabled.label"), create(translatable("trc.widgets.options.enabled.tooltip")), widgetConfiguration.isEnabled(), (button, value) -> widgetConfiguration.setEnabled(value == ON));
            optionsList.addColorButton(translatable("trc.widgets.options.color.label"), create(translatable("trc.widgets.options.color.tooltip")), widgetConfiguration.getColor(), (button, color) -> this.minecraft.setScreen(new ColorSelectionPopupScreen(this.minecraft.screen, color, value -> {
                widgetConfiguration.setColor(value);
                button.setColor(value);
            })));
            abstractTRCWidget.addOptions(optionsList);
        });
    }
}
