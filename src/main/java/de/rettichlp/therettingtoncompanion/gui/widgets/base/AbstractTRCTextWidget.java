package de.rettichlp.therettingtoncompanion.gui.widgets.base;

import de.rettichlp.therettingtoncompanion.gui.screens.WidgetPositionScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.widgetService;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.YELLOW;
import static java.lang.Math.toIntExact;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

public abstract class AbstractTRCTextWidget<C extends WidgetConfiguration> extends AbstractTRCWidget<C> {

    public abstract Component text();

    @Override
    public int getWidth() {
        int fontWidth = this.minecraft.font.width(text());
        return toIntExact(toNearestScale(fontWidth + 2 * TEXT_BOX_PADDING));
    }

    @Override
    public int getHeight() {
        return 3 * WIDGET_POSITION_SCALE;
    }

    @Override
    public void extractWidget(@NotNull GuiGraphicsExtractor graphics, int x, int y, Alignment alignment) {
        boolean isWidgetPositionScreen = this.minecraft.screen instanceof WidgetPositionScreen;

        graphics.fill(x, y, x + getWidth(), y + getHeight(), isWidgetPositionScreen ? BLUE.getRGB() : getBackgroundColor().getRGB());
        graphics.text(this.minecraft.font, text(), x + getWidth() / 2 - this.minecraft.font.width(text()) / 2, y + getHeight() / 2 - this.minecraft.font.lineHeight / 2, 0xFFFFFFFF, false);

        if (this.focused) {
            graphics.horizontalLine(0, graphics.guiWidth(), y, YELLOW.getRGB());
            graphics.verticalLine(x, 0, graphics.guiHeight(), YELLOW.getRGB());
        }

        if (isWidgetPositionScreen) {
            graphics.outline(x, y, getWidth(), getHeight(), CYAN.getRGB());
        }
    }

    public Color getBorderColor() {
        return getWidgetConfiguration().getColor();
    }

    public Color getBackgroundColor() {
        return widgetService.getSecondaryColor(getBorderColor());
    }

    protected MutableComponent keyValue(String key, String value) {
        return keyValue(key, literal(value));
    }

    protected MutableComponent keyValue(String key, Component value) {
        return empty()
                .append(literal(key).withStyle(GRAY))
                .append(literal(":").withStyle(DARK_GRAY)).append(" ")
                .append(value);
    }
}
