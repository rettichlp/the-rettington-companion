package de.rettichlp.therettingtoncompanion.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import org.jspecify.annotations.NonNull;

import static net.minecraft.network.chat.Component.empty;

public class ColorButton extends Button.Plain {

    private final int colorValue;

    public ColorButton(int x, int y, int width, int height, int colorValue, Button.OnPress onPress) {
        super(x, y, width, height, empty(), onPress, DEFAULT_NARRATION);
        this.colorValue = colorValue;
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);
        graphics.fill(getX() + 4, getY() + 4, getX() + getWidth() - 4, getY() + getHeight() - 4, this.colorValue);
    }
}
