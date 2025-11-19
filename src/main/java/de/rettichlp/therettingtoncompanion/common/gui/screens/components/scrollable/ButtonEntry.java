package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ButtonEntry extends ScrollableListEntry {

    private final ButtonWidget buttonWidget;

    public ButtonEntry(Text text, ButtonWidget.PressAction onPress) {
        this.buttonWidget = ButtonWidget.builder(text, onPress).build();
        this.buttonWidget.setWidth(getContentWidth());
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.buttonWidget.mouseClicked(click, doubled)) {
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.buttonWidget.setPosition(getContentMiddleX() - (this.buttonWidget.getWidth() / 2), getContentY());
        this.buttonWidget.render(context, mouseX, mouseY, deltaTicks);
    }
}
