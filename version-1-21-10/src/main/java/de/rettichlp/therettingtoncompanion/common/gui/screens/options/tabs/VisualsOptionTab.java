package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.configuration.VisualsConfiguration;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.named.CyclingButtonEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.named.ToggleButtonEntry;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.text.Text.translatable;

public class VisualsOptionTab extends AbstractOptionTab {

    public VisualsOptionTab() {
        super("visuals");
    }

    @Override
    public Text displayName() {
        return translatable("trc.option.visuals.title");
    }

    @Override
    public Collection<ScrollableListEntry> getContent() {
        ToggleButtonEntry showArmorHudEntry = new ToggleButtonEntry(
                translatable("trc.option.visuals.show_armor_hud.title"),
                translatable("trc.option.visuals.show_armor_hud.description"),
                configuration.visuals().isShowArmorHud(),
                value -> configuration.visuals().setShowArmorHud(value));

        ToggleButtonEntry showArrowHudEntry = new ToggleButtonEntry(
                translatable("trc.option.visuals.show_arrow_hud.title"),
                translatable("trc.option.visuals.show_arrow_hud.description"),
                configuration.visuals().isShowArrowHud(),
                value -> configuration.visuals().setShowArrowHud(value));

        CyclingButtonEntry<VisualsConfiguration.EquipmentModelVisibility> visibleEquipmentModelEntry = new CyclingButtonEntry<VisualsConfiguration.EquipmentModelVisibility>(
                translatable("trc.option.visuals.visible_equipment_model.title"),
                translatable("trc.option.visuals.visible_equipment_model.description"),
                configuration.visuals().getEquipmentModelVisibility(),
                List.of(VisualsConfiguration.EquipmentModelVisibility.values()),
                VisualsConfiguration.EquipmentModelVisibility::getDisplayName,
                equipmentModelVisibility -> configuration.visuals().setEquipmentModelVisibility(equipmentModelVisibility));

        CyclingButtonEntry<VisualsConfiguration.DayTimeValue> dayTimeValueEntry = new CyclingButtonEntry<VisualsConfiguration.DayTimeValue>(
                translatable("trc.option.visuals.day_time_value.title"),
                translatable("trc.option.visuals.day_time_value.description"),
                configuration.visuals().getDayTimeValue(),
                List.of(VisualsConfiguration.DayTimeValue.values()),
                VisualsConfiguration.DayTimeValue::getDisplayName,
                dayTimeValue -> configuration.visuals().setDayTimeValue(dayTimeValue));

        return List.of(showArmorHudEntry, showArrowHudEntry, visibleEquipmentModelEntry, dayTimeValueEntry);
    }
}
