package de.rettichlp.therettingtoncompanion.common.services;

import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.AbstractWidget;
import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.Widget;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.StreamSupport.stream;
import static org.atteo.classindex.ClassIndex.getAnnotated;

public class RenderService {

    public static final int TEXT_BOX_PADDING = 3;

    @Getter
    private LinkedHashSet<AbstractWidget<?>> widgets = new LinkedHashSet<>();

    public boolean isDebugEnabled() {
        return false;
    }

    public Color getSecondaryColor(@NotNull Color color) {
        return new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2, 100);
    }

    public void initializeWidgets() {
        this.widgets = stream(getAnnotated(Widget.class).spliterator(), false)
                .map(widgetClass -> {
                    try {
                        return (AbstractWidget<?>) widgetClass.getConstructor().newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .peek(AbstractWidget::init)
                .sorted(comparing(AbstractWidget::getRegistryName))
                .collect(toCollection(LinkedHashSet::new));
    }

    public void renderShadowText(@NonNull GuiGraphicsExtractor graphics, Component text, int y, int color, int shadowColor) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);
        renderShadowText(graphics, text, (graphics.guiWidth() - textWidth) / 2, y, color, shadowColor);
    }

    private void renderShadowText(@NonNull GuiGraphicsExtractor graphics, Component text, int x, int y, int color, int shadowColor) {
        Font font = Minecraft.getInstance().font;

        // render shadow
        graphics.text(font, text, x + 1, y, shadowColor, false);
        graphics.text(font, text, x - 1, y, shadowColor, false);
        graphics.text(font, text, x, y + 1, shadowColor, false);
        graphics.text(font, text, x, y - 1, shadowColor, false);

        // render text
        graphics.text(font, text, x, y, color, false);
    }
}
