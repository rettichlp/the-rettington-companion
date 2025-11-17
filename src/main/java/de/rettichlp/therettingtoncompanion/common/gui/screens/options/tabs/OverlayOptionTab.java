package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

import static net.minecraft.text.Text.translatable;

public class OverlayOptionTab extends AbstractOptionTab {

    public OverlayOptionTab() {
        super("overlay");
    }

    @Override
    public Text displayName() {
        return translatable("trc.option.overlay.title");
    }

    @Override
    public Collection<ScrollableListEntry> getContent() {
        return List.of();
    }
}
