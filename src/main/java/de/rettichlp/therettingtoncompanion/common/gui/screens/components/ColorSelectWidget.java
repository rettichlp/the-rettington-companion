package de.rettichlp.therettingtoncompanion.common.gui.screens.components;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static net.minecraft.text.Text.empty;

public class ColorSelectWidget extends ButtonWidget {

    private static final List<Formatting> COLORS = stream(Formatting.values()).filter(Formatting::isColor).toList();

    private final Consumer<Formatting> onPress;

    private Formatting currentFormatting;

    public ColorSelectWidget(int width, int height, @NotNull Formatting initialFormatting, Consumer<Formatting> onPress) {
        super(0, 0, width, height, empty(), button -> {}, null);

        if (!initialFormatting.isColor()) {
            throw new IllegalArgumentException("Initial formatting must be a color.");
        }

        this.onPress = onPress;
        this.currentFormatting = initialFormatting;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);

        assert this.currentFormatting.getColorValue() != null;
        context.fill(getX() + 2, getY() + 2, getX() + getWidth() - 2, getY() + getHeight() - 2, 0xFF000000 | this.currentFormatting.getColorValue());
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int button = click.button();
        if (button == 0) { // left
            nextFormatting();
            this.onPress.accept(this.currentFormatting);
            return true;
        } else if (button == 1) { // right
            previousFormatting();
            this.onPress.accept(this.currentFormatting);
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        System.out.println("mouseScrolled: " + verticalAmount + " and horizontalAmount: " + horizontalAmount);
        if (verticalAmount > 0) {
            nextFormatting();
            return true;
        } else if (verticalAmount < 0) {
            previousFormatting();
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void nextFormatting() {
        int currentIndex = COLORS.indexOf(this.currentFormatting);
        int nextIndex = (currentIndex + 1) % COLORS.size();
        this.currentFormatting = COLORS.get(nextIndex);
    }

    private void previousFormatting() {
        int currentIndex = COLORS.indexOf(this.currentFormatting);
        int previousIndex = currentIndex - 1;
        this.currentFormatting = previousIndex < 0 ? COLORS.getLast() : COLORS.get(previousIndex);
    }
}
