package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ToggleButtonEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.text.Text.translatable;

public class OverlayOptionTab extends AbstractOptionTab {

    public OverlayOptionTab() {
        super("overlay");
    }

    @Override
    public Text displayName() {
        return translatable("trc.option.overlay.title");
    }

    @Override
    public Collection<ScrollableListEntry> getContent() {
        ToggleButtonEntry showArmorHudEntry = new ToggleButtonEntry(translatable("trc.option.overlay.show_armor_hud.title"), configuration.isShowArmorHud(), configuration::setShowArmorHud);
        ToggleButtonEntry showArrowHudEntry = new ToggleButtonEntry(translatable("trc.option.overlay.show_arrow_hud.title"), configuration.isShowArrowHud(), configuration::setShowArrowHud);

        return List.of(showArmorHudEntry, showArrowHudEntry);
    }
}
