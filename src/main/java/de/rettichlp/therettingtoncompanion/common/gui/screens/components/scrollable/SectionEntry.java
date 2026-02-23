package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class SectionEntry extends ScrollableListEntry {

    private final Text text;
    private final TextWidget textWidget;

    public SectionEntry(Text text) {
        this.text = text;
        this.textWidget = new TextWidget(text, MinecraftClient.getInstance().textRenderer);

        this.children.add(this.textWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.textWidget.setPosition(getContentMiddleX() - (this.client.textRenderer.getWidth(this.text) / 2), getContentMiddleY());
        this.textWidget.render(context, mouseX, mouseY, deltaTicks);
    }
}
