package de.rettichlp.therettingtoncompanion.gui.options.list;

import lombok.NoArgsConstructor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static java.util.Collections.emptyList;

@NoArgsConstructor
public abstract class AbstractEntry extends ContainerObjectSelectionList.Entry<AbstractEntry> {

    @Override
    public @NonNull List<? extends NarratableEntry> narratables() {
        return emptyList();
    }
}
