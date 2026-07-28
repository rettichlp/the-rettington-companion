package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.list.FullWidthButtonEntry;
import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.screens.ColorSelectionPopupScreen;
import de.rettichlp.therettingtoncompanion.gui.screens.WidgetPositionScreen;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.widgetService;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.literal;
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
        FullWidthButtonEntry fullWidthButtonEntry = optionsList.addFullWidthButton(translatable("trc.option.widgets.position.label"), create(translatable("trc.option.widgets.position.tooltip")), _ -> this.minecraft.gui.setScreen(new WidgetPositionScreen(this.minecraft.gui.screen())));
        fullWidthButtonEntry.getButton().active = this.minecraft.level != null;
        optionsList.addFullWidthSlider(translatable("trc.option.widgets.size.label"), 4, 16, configuration.widgets().getSize(), value -> configuration.widgets().setSize(value));
        optionsList.addFullWidthSlider(translatable("trc.option.widgets.padding.label"), 0, 5, configuration.widgets().getPadding(), value -> configuration.widgets().setPadding(value));

        widgetService.getInitializedWidgets().forEach(abstractTRCWidget -> {
            Component label = abstractTRCWidget.getLabel().copy()
                    .append(literal(" - ").withStyle(style -> style.withBold(false)))
                    .append(abstractTRCWidget.getTooltip().copy().withStyle(style -> style.withBold(false)));
            optionsList.addHeader(label);

            WidgetConfiguration widgetConfiguration = abstractTRCWidget.getWidgetConfiguration();
            optionsList.addToggleButton(translatable("trc.widgets.options.enabled.label"), create(translatable("trc.widgets.options.enabled.tooltip")), widgetConfiguration.isEnabled(), (button, value) -> widgetConfiguration.setEnabled(value == ON));
            optionsList.addColorButton(translatable("trc.widgets.options.color.label"), create(translatable("trc.widgets.options.color.tooltip")), widgetConfiguration.getColor(), (button, color) -> this.minecraft.gui.setScreen(new ColorSelectionPopupScreen(this.minecraft.gui.screen(), color, value -> {
                widgetConfiguration.setColor(value);
                button.setColor(value);
            })));
            optionsList.addToggleButton(translatable("trc.widgets.options.background_enabled.label"), create(translatable("trc.widgets.options.background_enabled.tooltip")), widgetConfiguration.isBackgroundEnabled(), (_, value) -> widgetConfiguration.setBackgroundEnabled(value == ON));
            abstractTRCWidget.addOptions(optionsList);
        });
    }
}
