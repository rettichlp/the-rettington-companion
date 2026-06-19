package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.models.GammaPreset;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.translatable;

public class GeneralOptionsTab extends AbstractTRCOptionsTab {

    public GeneralOptionsTab() {
        super("general");
    }

    @Override
    public Component title() {
        return translatable("trc.option.general.title");
    }

    @Override
    public void populateOptionsList(@NonNull TRCOptionsList optionsList) {
        optionsList.addCycleButton(translatable("trc.option.general.gamma_preset.label"), create(translatable("trc.option.general.gamma_preset.tooltip")), configuration.getGammaPreset(), List.of(GammaPreset.values()), (_, value) -> configuration.setGammaPreset(value));
    }
}
