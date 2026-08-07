package de.rettichlp.therettingtoncompanion.gui.widgets.base;

import de.rettichlp.therettingtoncompanion.gui.ICycleButtonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.util.List;

import static java.lang.Math.toIntExact;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.translatable;

@RequiredArgsConstructor
public abstract class AbstractTRCWidgetGroup<C extends WidgetConfiguration> extends AbstractTRCWidget<C> {

    private static final int SPACE_BETWEEN = 2;

    public abstract List<? extends AbstractTRCWidget<?>> widgets();

    public abstract Alignment alignment();

    @Override
    public int getWidth() {
        // only for position calculation (sub-widgets can have more width)
        return toIntExact(toNearestScale(this.minecraft.getWindow().getGuiScaledWidth() / 3.0));
    }

    @Override
    public int getHeight() {
        // only for position calculation (sub-widgets can have more height)
        return toIntExact(toNearestScale(100));
    }

    @Override
    public void extractWidget(@NotNull GuiGraphicsExtractor graphics, int x, int y, Color color, boolean backgroundEnabled, boolean textShadowEnabled) {
        List<? extends AbstractTRCWidget<?>> widgets = widgets();
        for (int i = 0; i < widgets.size(); i++) {
            AbstractTRCWidget<?> widget = widgets.get(i);

            int widgetX;
            int widgetY = y + i * (widget.getHeight() + SPACE_BETWEEN);

            switch (alignment()) {
                case LEFT -> widgetX = x;
                case CENTER -> widgetX = (int) (getMiddle(x) - widget.getWidth() / 2.0);
                case RIGHT -> widgetX = (int) (getRight(x) - widget.getWidth());
                default -> throw new IllegalStateException("Unexpected value: " + alignment());
            }

            widget.extractWidget(graphics, widgetX, widgetY, color, backgroundEnabled, textShadowEnabled);
        }
    }

    @AllArgsConstructor
    public enum Alignment implements ICycleButtonValue {

        LEFT(translatable("trc.alignment.left")),
        CENTER(translatable("trc.alignment.center")),
        RIGHT(translatable("trc.alignment.right"));

        @Getter
        @Accessors(fluent = true)
        private final Component value;

        @Contract(" -> new")
        @Override
        public @NonNull Tooltip tooltip() {
            return create(empty());
        }
    }
}
