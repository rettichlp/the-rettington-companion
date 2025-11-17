package de.rettichlp.therettingtoncompanion.common.gui.screens.components.tab;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;

import java.util.Collection;

import static net.fabricmc.api.EnvType.CLIENT;

@Environment(CLIENT)
public abstract class TabMenuWidget extends ElementListWidget<ScrollableListEntry> {

    public TabMenuWidget(int width, int height, int x, int y) {
        super(MinecraftClient.getInstance(), width, height, y, 24);
        setPosition(x, y);
        getContent().forEach(this::addEntry);
    }

    public abstract Collection<ScrollableListEntry> getContent();
}
