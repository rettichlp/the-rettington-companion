package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable;

import lombok.NoArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public abstract class ScrollableListEntry extends ElementListWidget.Entry<ScrollableListEntry> {

    protected final MinecraftClient client = MinecraftClient.getInstance();

    protected final List<ClickableWidget> children = new ArrayList<>();

    @Override
    public List<? extends Selectable> selectableChildren() {
        return this.children;
    }

    @Override
    public List<? extends Element> children() {
        return this.children;
    }

    @Override
    public int getContentX() {
        return (this.client.getWindow().getScaledWidth() - getContentWidth()) / 2;
    }

    @Override
    public int getContentWidth() {
        return 350;
    }
}
