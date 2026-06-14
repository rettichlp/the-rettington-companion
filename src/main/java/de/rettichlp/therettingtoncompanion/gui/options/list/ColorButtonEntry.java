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
import java.util.function.Consumer;

import static net.minecraft.network.chat.Component.empty;

public class ColorButtonEntry extends AbstractEntry {

    private final StringWidget stringWidget;
    private final Button button;

    private int colorValue;

    protected ColorButtonEntry(Font font, Component label, Tooltip tooltip, int initialValue, Consumer<Integer> valueChangeListener) {
        this.stringWidget = new StringWidget(label, font);
        this.stringWidget.setTooltip(tooltip);
        this.colorValue = initialValue;
        this.button = Button.builder(empty(), _ -> valueChangeListener.accept(this.colorValue))
                .size(100, 20)
                .build();
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.stringWidget.setPosition(getContentX(), getContentYMiddle() - this.stringWidget.getHeight() / 2);
        this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);

        this.button.setPosition(getContentRight() - this.button.getWidth() + 2, getContentYMiddle() - this.button.getHeight() / 2);
        this.button.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.fill(this.button.getX() + 2, this.button.getY() + 2, this.button.getX() + this.button.getWidth() - 2, this.button.getY() + this.button.getHeight() - 2, this.colorValue);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.stringWidget, this.button);
    }
}
