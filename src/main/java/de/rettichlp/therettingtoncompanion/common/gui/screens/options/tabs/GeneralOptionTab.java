package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.named.CyclingButtonEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.named.ToggleButtonEntry;
import de.rettichlp.therettingtoncompanion.common.models.GammaPreset;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.text.Text.translatable;

public class GeneralOptionTab extends AbstractOptionTab {

    public GeneralOptionTab() {
        super("general");
    }

    @Override
    public Text displayName() {
        return translatable("trc.option.general.title");
    }

    @Override
    public Collection<ScrollableListEntry> getContent() {
        CyclingButtonEntry<GammaPreset> cyclingButtonEntry = new CyclingButtonEntry<>(
                translatable("trc.option.general.gamma_preset.title"),
                translatable("trc.option.general.gamma_preset.description"),
                configuration.getGammaPreset(),
                List.of(GammaPreset.values()),
                GammaPreset::getDisplayName,
                configuration::setGammaPreset);

        ToggleButtonEntry toggleButtonEntry = new ToggleButtonEntry(
                translatable("trc.option.general.hide_armor.title"),
                translatable("trc.option.general.hide_armor.description"),
                configuration.isHideArmor(),
                configuration::setHideArmor);

        return List.of(cyclingButtonEntry, toggleButtonEntry);
    }
}
