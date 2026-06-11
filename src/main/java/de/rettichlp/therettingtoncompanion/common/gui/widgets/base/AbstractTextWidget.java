package de.rettichlp.therettingtoncompanion.common.gui.widgets.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.renderService;
import static de.rettichlp.therettingtoncompanion.common.services.RenderService.TEXT_BOX_PADDING;
import static java.awt.Color.WHITE;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

public abstract class AbstractTextWidget<C extends WidgetConfiguration> extends AbstractWidget<C> {

    public abstract Component text();

    @Override
    public int getWidth() {
        int fontWidth = getFont().width(text());
        return fontWidth + 2 * TEXT_BOX_PADDING;
    }

    @Override
    public int getHeight() {
        return getFont().lineHeight + 2 * TEXT_BOX_PADDING;
    }

    @Override
    public void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y, Alignment alignment) {
        graphics.fill(x, y, x + getWidth(), y + getHeight(), getBackgroundColor().getRGB());
        graphics.text(getFont(), text(), x + TEXT_BOX_PADDING, y + TEXT_BOX_PADDING, 0xFFFFFFFF, false);
    }

    public Color getColor() {
        return WHITE;
    }

    public Color getBackgroundColor() {
        return renderService.getSecondaryColor(getColor());
    }

    protected MutableComponent keyValue(String key, String value) {
        return keyValue(key, literal(value));
    }

    protected MutableComponent keyValue(String key, Component value) {
        return empty()
                .append(literal(key).copy().withStyle(GRAY))
                .append(literal(":").copy().withStyle(DARK_GRAY)).append(" ")
                .append(value);
    }
}
