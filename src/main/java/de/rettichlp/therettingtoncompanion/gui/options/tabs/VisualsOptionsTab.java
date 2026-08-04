package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration;
import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.screens.ColorSelectionPopupScreen;
import de.rettichlp.therettingtoncompanion.models.GammaPreset;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static java.util.Arrays.asList;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.translatable;

public class VisualsOptionsTab extends AbstractTRCOptionsTab {

    public VisualsOptionsTab() {
        super("visuals");
    }

    @Override
    public Component title() {
        return translatable("trc.option.visuals.title");
    }

    @Override
    public void populateOptionsList(@NonNull TRCOptionsList optionsList) {
        optionsList.addHeader(translatable("debug.entry.overlay"), null);
        optionsList.addToggleButton(translatable("trc.option.visuals.show_armor_hud.label"), create(translatable("trc.option.visuals.show_armor_hud.tooltip")), configuration.visuals().isShowArmorHud(), (_, value) -> configuration.visuals().setShowArmorHud(value == ON));
        optionsList.addToggleButton(translatable("trc.option.visuals.show_arrow_hud.label"), create(translatable("trc.option.visuals.show_arrow_hud.tooltip")), configuration.visuals().isShowArrowHud(), (_, value) -> configuration.visuals().setShowArrowHud(value == ON));
        optionsList.addColorButton(translatable("trc.option.visuals.experience_level_color.label"), create(translatable("trc.option.visuals.experience_level_color.tooltip")), new Color(configuration.visuals().getExperienceLevelColor()), (colorButton, value) -> this.minecraft.gui.setScreen(new ColorSelectionPopupScreen(optionsList.getScreen(), value, color -> {
            configuration.visuals().setExperienceLevelColor(color.getRGB());
            colorButton.setColor(color);
        })));
        optionsList.addToggleButton(translatable("trc.option.visuals.show_empty_inventory_slot_count.label"), create(translatable("trc.option.visuals.show_empty_inventory_slot_count.tooltip")), configuration.visuals().isShowEmptyInventorySlotCount(), (_, value) -> configuration.visuals().setShowEmptyInventorySlotCount(value == ON));

        optionsList.addHeader(translatable("createWorld.tab.world.title"), null);
        optionsList.addCycleButton(translatable("trc.option.visuals.visible_equipment_model.label"), create(translatable("trc.option.visuals.visible_equipment_model.tooltip")), configuration.visuals().getEquipmentModelVisibility(), asList(VisualsConfiguration.EquipmentModelVisibility.values()), (_, value) -> configuration.visuals().setEquipmentModelVisibility(value));
        optionsList.addCycleButton(translatable("trc.option.visuals.gamma_preset.label"), create(translatable("trc.option.visuals.gamma_preset.tooltip")), configuration.visuals().getGammaPreset(), List.of(GammaPreset.values()), (_, value) -> configuration.visuals().setGammaPreset(value));

        optionsList.addHeader(translatable("soundCategory.ambient"), null);
        optionsList.addCycleButton(translatable("trc.option.visuals.day_time_value.label"), create(translatable("trc.option.visuals.day_time_value.tooltip")), configuration.visuals().getDayTimeValue(), asList(VisualsConfiguration.DayTimeValue.values()), (_, value) -> configuration.visuals().setDayTimeValue(value));
        optionsList.addWeatherButton(translatable("trc.option.visuals.weather_value.label"), create(translatable("trc.option.visuals.weather_value.tooltip")), weatherValue -> configuration.visuals().setWeatherValue(weatherValue));
    }
}
