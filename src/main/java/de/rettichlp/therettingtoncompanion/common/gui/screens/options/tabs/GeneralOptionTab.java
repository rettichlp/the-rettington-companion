package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

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
        return List.of();
    }
}
