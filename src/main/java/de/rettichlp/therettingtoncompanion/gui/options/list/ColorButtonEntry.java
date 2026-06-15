package de.rettichlp.therettingtoncompanion.gui.options.list;

import de.rettichlp.therettingtoncompanion.gui.ColorButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.util.List;
import java.util.function.BiConsumer;

public class ColorButtonEntry extends AbstractEntry {

    private final StringWidget stringWidget;
    private final ColorButton colorButton;

    protected ColorButtonEntry(Font font, Component label, Tooltip tooltip, Color color, BiConsumer<ColorButton, Color> onPress) {
        this.stringWidget = new StringWidget(label, font);
        this.stringWidget.setTooltip(tooltip);
        this.colorButton = new ColorButton(0, 0, 100, 20, color, button -> onPress.accept((ColorButton) button, color));
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.stringWidget.setPosition(getContentX(), getContentYMiddle() - this.stringWidget.getHeight() / 2);
        this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);

        this.colorButton.setPosition(getContentRight() - this.colorButton.getWidth() + 2, getContentYMiddle() - this.colorButton.getHeight() / 2);
        this.colorButton.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.stringWidget, this.colorButton);
    }
}
