package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.named.ToggleButtonEntry;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.text.Text.translatable;

public class InventoryOptionTab extends AbstractOptionTab {

    public InventoryOptionTab() {
        super("inventory");
    }

    @Override
    public Text displayName() {
        return translatable("trc.option.inventory.title");
    }

    @Override
    public Collection<ScrollableListEntry> getContent() {
        ToggleButtonEntry instantQuickMoveEntry = new ToggleButtonEntry(
                translatable("trc.option.inventory.instant_quick_move.title"),
                translatable("trc.option.inventory.instant_quick_move.description"),
                configuration.inventory().isInstantQuickMove(),
                value -> configuration.inventory().setInstantQuickMove(value));

        ToggleButtonEntry autoRestockEntry = new ToggleButtonEntry(
                translatable("trc.option.inventory.auto_restock.title"),
                translatable("trc.option.inventory.auto_restock.description"),
                configuration.inventory().isAutoRestock(),
                value -> configuration.inventory().setAutoRestock(value));

        return List.of(instantQuickMoveEntry, autoRestockEntry);
    }
}
