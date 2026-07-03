package de.rettichlp.therettingtoncompanion.gui.widgets.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.widgetService;
import static java.lang.Math.toIntExact;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

public abstract class AbstractTRCTextWidget<C extends WidgetConfiguration> extends AbstractTRCWidget<C> {

    public abstract Component text();

    @Override
    public int getWidth() {
        int textWidth = this.minecraft.font.width(text());
        return toIntExact(toNearestScale(textWidth + 2 * TEXT_BOX_PADDING));
    }

    @Override
    public int getHeight() {
        return 3 * WIDGET_POSITION_SCALE;
    }

    @Override
    public void extractWidget(@NotNull GuiGraphicsExtractor graphics, int x, int y, Alignment alignment) {
        graphics.fill(x, y, x + getWidth(), y + getHeight(), getBackgroundColor().getRGB());
        graphics.text(this.minecraft.font, text(), x + getWidth() / 2 - this.minecraft.font.width(text()) / 2, y + getHeight() / 2 - this.minecraft.font.lineHeight / 2, getWidgetConfiguration().getColor().getRGB(), false);

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
