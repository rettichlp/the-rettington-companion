package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.TRCOptionsList;
import net.minecraft.network.chat.Component;

import static net.minecraft.network.chat.Component.translatable;

public class InventoryOptionsTab extends AbstractTRCOptionsTab {

    public InventoryOptionsTab() {
        super("inventory");
    }

    @Override
    public Component title() {
        return translatable("trc.option.inventory.title");
    }

    @Override
    public void populateOptionsList(TRCOptionsList optionsList) {

    }
}
