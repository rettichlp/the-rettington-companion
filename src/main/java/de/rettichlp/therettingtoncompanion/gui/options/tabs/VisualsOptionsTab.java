package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.TRCOptionsList;
import net.minecraft.network.chat.Component;

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
    public void populateOptionsList(TRCOptionsList optionsList) {

    }
}
