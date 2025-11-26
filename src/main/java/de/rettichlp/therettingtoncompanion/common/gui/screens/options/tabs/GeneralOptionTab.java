package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.named.CyclingButtonEntry;
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
        CyclingButtonEntry<GammaPreset> gammaPresetEntry = new CyclingButtonEntry<>(
                translatable("trc.option.general.gamma_preset.title"),
                translatable("trc.option.general.gamma_preset.description"),
                configuration.getGammaPreset(),
                List.of(GammaPreset.values()),
                GammaPreset::getDisplayName,
                configuration::setGammaPreset);

        return List.of(gammaPresetEntry);
    }
}
