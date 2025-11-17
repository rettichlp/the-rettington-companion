package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

import static net.minecraft.screen.ScreenTexts.OFF;
import static net.minecraft.screen.ScreenTexts.ON;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

public class ToggleButtonEntry extends NamedEntry {

    private final ButtonWidget buttonWidget;

    private boolean buttonValue;

    public ToggleButtonEntry(Text name, Text tooltip, boolean initialValue, Consumer<Boolean> onValueChange) {
        super(name);
        this.buttonValue = initialValue;

        this.buttonWidget = ButtonWidget.builder(getText(), button -> {
            this.buttonValue = !this.buttonValue;
            onValueChange.accept(this.buttonValue);
            updateText();
        }).build();

        this.buttonWidget.setWidth(60);
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

    public void updateText() {
        this.buttonWidget.setMessage(getText());
    }

    private Text getText() {
        return this.buttonValue ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED);
    }
}
