package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration;
import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.screens.ColorSelectionPopupScreen;
import de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen;
import de.rettichlp.therettingtoncompanion.models.GammaPreset;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.visualsService;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.DEFAULT_DAMAGE_OVERLAY_COLOR;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.DEFAULT_DAMAGE_OVERLAY_OPACITY;
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

        optionsList.addHeader(translatable("trc.option.visuals.nametag.section_title"), null);
        optionsList.addToggleButton(translatable("trc.option.visuals.nametag.show_own.label"), create(translatable("trc.option.visuals.nametag.show_own.tooltip")), configuration.visuals().isNametagShowOwn(), (_, value) -> configuration.visuals().setNametagShowOwn(value == ON));
        optionsList.addToggleButton(translatable("trc.option.visuals.nametag.text_shadow.label"), create(translatable("trc.option.visuals.nametag.text_shadow.tooltip")), configuration.visuals().isNametagTextShadow(), (_, value) -> configuration.visuals().setNametagTextShadow(value == ON));

        optionsList.addHeader(translatable("trc.option.visuals.effects.section_title"), null);
        optionsList.addToggleButton(translatable("trc.option.visuals.effects.show_all_icons.label"), create(translatable("trc.option.visuals.effects.show_all_icons.tooltip")), configuration.visuals().isEffectShowAllIcons(), (_, value) -> configuration.visuals().setEffectShowAllIcons(value == ON));
        optionsList.addToggleButton(translatable("trc.option.visuals.effects.show_duration_timer.label"), create(translatable("trc.option.visuals.effects.show_duration_timer.tooltip")), configuration.visuals().isEffectShowDurationTimer(), (_, value) -> configuration.visuals().setEffectShowDurationTimer(value == ON));

        optionsList.addHeader(translatable("trc.option.visuals.damage_overlay.section_title"), null);
        optionsList.addColorButton(translatable("trc.option.visuals.damage_overlay.color.label"), create(translatable("trc.option.visuals.damage_overlay.color.tooltip")), new Color(configuration.visuals().getDamageOverlayColor()), (colorButton, value) -> this.minecraft.gui.setScreen(new ColorSelectionPopupScreen(optionsList.getScreen(), value, color -> {
            configuration.visuals().setDamageOverlayColor(color.getRGB() & 0xFFFFFF);
            colorButton.setColor(color);
            visualsService.refreshDamageOverlayColor();
        })));
        optionsList.addFullWidthSlider(translatable("trc.option.visuals.damage_overlay.opacity.label"), 0, 100, configuration.visuals().getDamageOverlayOpacity(), value -> {
            configuration.visuals().setDamageOverlayOpacity(value);
            visualsService.refreshDamageOverlayColor();
        });
        optionsList.addFullWidthButton(translatable("trc.option.visuals.damage_overlay.reset.label"), create(translatable("trc.option.visuals.damage_overlay.reset.tooltip")), _ -> {
            configuration.visuals().setDamageOverlayColor(DEFAULT_DAMAGE_OVERLAY_COLOR);
            configuration.visuals().setDamageOverlayOpacity(DEFAULT_DAMAGE_OVERLAY_OPACITY);
            visualsService.refreshDamageOverlayColor();
            this.minecraft.gui.setScreen(new TRCOptionsScreen(getId(), optionsList.getScreen().getLastScreen(), optionsList.getScreen().isRenderBackground()));
        });
    }
}
