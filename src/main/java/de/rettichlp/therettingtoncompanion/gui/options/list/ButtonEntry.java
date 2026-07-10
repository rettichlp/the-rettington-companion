package de.rettichlp.therettingtoncompanion.gui.options.list;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ButtonEntry extends AbstractEntry {

    private final StringWidget stringWidget;
    private final Button button;

    protected ButtonEntry(Font font, Component label, Component caption, Tooltip tooltip, Button.OnPress onPress) {
        this.stringWidget = new StringWidget(label, font);
        this.stringWidget.setTooltip(tooltip);
        this.button = Button.builder(caption, onPress)
                .size(100, 20)
                .tooltip(tooltip)
                .build();
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.stringWidget.setPosition(getContentX(), getContentYMiddle() - this.stringWidget.getHeight() / 2);
        this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);

        this.button.setPosition(getContentRight() - this.button.getWidth() + 2, getContentYMiddle() - this.button.getHeight() / 2);
        this.button.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.stringWidget, this.button);
    }
}
