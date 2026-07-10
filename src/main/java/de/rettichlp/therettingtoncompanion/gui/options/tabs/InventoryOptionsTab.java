package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static net.minecraft.client.gui.components.Tooltip.create;
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
    public void populateOptionsList(@NonNull TRCOptionsList optionsList) {
        optionsList.addToggleButton(translatable("trc.option.inventory.instant_quick_move.label"), create(translatable("trc.option.inventory.instant_quick_move.tooltip")), configuration.inventory().isInstantQuickMove(), (_, value) -> configuration.inventory().setInstantQuickMove(value == ON));
        optionsList.addToggleButton(translatable("trc.option.inventory.auto_restock.label"), create(translatable("trc.option.inventory.auto_restock.tooltip")), configuration.inventory().isAutoRestock(), (_, value) -> configuration.inventory().setAutoRestock(value == ON));
    }
}
