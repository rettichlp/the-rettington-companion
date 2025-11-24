package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.named;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CyclingButtonEntry<E> extends NamedEntry {

    private final List<E> elements;
    private final Function<E, Text> valueToName;
    private final Consumer<E> onChange;
    private final ButtonWidget buttonWidget;

    private E currentValue;

    public CyclingButtonEntry(Text name,
                              Text tooltip,
                              E initialValue,
                              List<E> elements,
                              Function<E, Text> valueToName,
                              Consumer<E> onChange) {
        super(name);
        this.currentValue = initialValue;
        this.elements = elements;
        this.valueToName = valueToName;
        this.onChange = onChange;

        this.buttonWidget = ButtonWidget.builder(getText(), button -> {}).build();

        this.buttonWidget.setWidth(150);
        this.buttonWidget.setTooltip(Tooltip.of(tooltip));

        this.children.add(this.buttonWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.drawName(context);
        this.buttonWidget.setX(getContentRightEnd() - this.buttonWidget.getWidth());
        this.buttonWidget.setY(getContentY());
        this.buttonWidget.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(@NotNull Click click, boolean doubled) {
        int button = click.button();
        boolean mouseOver = this.buttonWidget.isMouseOver(click.x(), click.y());

        if (button == 0 && mouseOver) { // left
            nextElement();
            return true;
        } else if (button == 1 && mouseOver) { // right
            previousElement();
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean mouseOver = this.buttonWidget.isMouseOver(mouseX, mouseY);

        if (verticalAmount > 0 && mouseOver) {
            previousElement();
            return true;
        } else if (verticalAmount < 0 && mouseOver) {
            nextElement();
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void nextElement() {
        int currentIndex = this.elements.indexOf(this.currentValue);
        int nextIndex = (currentIndex + 1) % this.elements.size();
        this.currentValue = this.elements.get(nextIndex);
        this.onChange.accept(this.currentValue);
        updateText();
    }

    private void previousElement() {
        int currentIndex = this.elements.indexOf(this.currentValue);
        int previousIndex = currentIndex - 1;
        this.currentValue = previousIndex < 0 ? this.elements.getLast() : this.elements.get(previousIndex);
        this.onChange.accept(this.currentValue);
        updateText();
    }

    private void updateText() {
        this.buttonWidget.setMessage(getText());
    }

    private Text getText() {
        return this.valueToName.apply(this.currentValue);
    }
}
