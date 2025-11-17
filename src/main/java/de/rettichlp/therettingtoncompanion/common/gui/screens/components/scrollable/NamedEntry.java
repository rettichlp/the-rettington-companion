package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public abstract class NamedEntry extends ScrollableListEntry {

    private final Text name;

    public NamedEntry(Text name) {
        this.name = name;
    }

    public void drawName(@NotNull DrawContext context) {
        context.drawTextWithShadow(this.client.textRenderer, this.name, getContentX() + 5, getContentMiddleY() - (this.client.textRenderer.fontHeight / 2), -1);
    }
}
