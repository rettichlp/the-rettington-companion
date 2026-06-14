package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.TRCOptionsList;
import net.minecraft.network.chat.Component;

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
    public void populateOptionsList(TRCOptionsList optionsList) {

    }
}
