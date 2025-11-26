package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
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

        return List.of(showArmorHudEntry, showArrowHudEntry);
    }
}
