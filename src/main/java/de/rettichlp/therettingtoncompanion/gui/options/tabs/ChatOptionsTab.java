package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.TRCOptionsList;
import net.minecraft.network.chat.Component;

import static net.minecraft.network.chat.Component.translatable;

public class ChatOptionsTab extends AbstractTRCOptionsTab {

    public ChatOptionsTab() {
        super("chat");
    }

    @Override
    public Component title() {
        return translatable("trc.option.chat.title");
    }

    @Override
    public void populateOptionsList(TRCOptionsList optionsList) {

    }
}
