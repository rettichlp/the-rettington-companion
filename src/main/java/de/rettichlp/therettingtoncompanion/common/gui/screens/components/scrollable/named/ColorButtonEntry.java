package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.named;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.ColorButtonWidget;
import de.rettichlp.therettingtoncompanion.common.gui.screens.popup.ColorReturningSelectionPopupScreen;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.function.Consumer;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;

public class ColorButtonEntry extends NamedEntry {

    private ColorButtonWidget colorButtonWidget = null;

    public ColorButtonEntry(Text name, Text tooltip, Consumer<Color> onChange) {
        super(name);

        Color currentColourValue = new Color(configuration.visuals().getExperienceLevelColor());
        this.colorButtonWidget = new ColorButtonWidget(60, 20, currentColourValue, color -> {
            ColorReturningSelectionPopupScreen colorReturningSelectionPopupScreen = new ColorReturningSelectionPopupScreen(this.client.currentScreen, color1 -> {
                onChange.accept(color1);
                this.colorButtonWidget.updateColor(color1);
            }, currentColourValue);
            this.client.setScreen(colorReturningSelectionPopupScreen);
        });
        this.colorButtonWidget.setTooltip(Tooltip.of(tooltip));

        this.children.add(this.colorButtonWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.drawName(context);
        this.colorButtonWidget.setX(getContentRightEnd() - this.colorButtonWidget.getWidth());
        this.colorButtonWidget.setY(getContentY());
        this.colorButtonWidget.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(@NotNull Click click, boolean doubled) {
        if (this.colorButtonWidget != null && this.colorButtonWidget.mouseClicked(click, doubled)) {
            return true;
        }

        return super.mouseClicked(click, doubled);
    }
}
