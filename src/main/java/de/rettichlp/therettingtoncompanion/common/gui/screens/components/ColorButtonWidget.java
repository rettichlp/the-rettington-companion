package de.rettichlp.therettingtoncompanion.common.gui.screens.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.awt.Color;
import java.util.function.Consumer;

import static net.minecraft.text.Text.empty;

public class ColorButtonWidget extends ButtonWidget {

    private final Color color;

    public ColorButtonWidget(int width, int height, Color color, Consumer<Color> onPressed) {
        super(0, 0, width, height, empty(), button -> onPressed.accept(color), textSupplier -> empty());
        this.color = color;
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        drawButton(context);
        context.fill(getX() + 2, getY() + 2, getX() + getWidth() - 2, getY() + getHeight() - 2, 0xFF000000 | this.color.getRGB());
    }
}
