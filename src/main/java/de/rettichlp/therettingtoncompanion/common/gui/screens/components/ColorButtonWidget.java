package de.rettichlp.therettingtoncompanion.common.gui.screens.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static net.minecraft.text.Text.empty;

public class ColorButtonWidget extends ButtonWidget {

    private final Formatting color;

    public ColorButtonWidget(int width, int height, @NotNull Formatting color, Consumer<Formatting> onPressed) {
        super(0, 0, width, height, empty(), button -> onPressed.accept(color), textSupplier -> empty());

        if (!color.isColor()) {
            throw new IllegalArgumentException("Formatting must be a color.");
        }

        this.color = color;
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        drawButton(context);

        assert this.color.getColorValue() != null;
        context.fill(getX() + 2, getY() + 2, getX() + getWidth() - 2, getY() + getHeight() - 2, 0xFF000000 | this.color.getColorValue());
    }
}
