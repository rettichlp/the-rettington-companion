package de.rettichlp.therettingtoncompanion.gui.options.list;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

import static java.lang.Math.clamp;
import static java.lang.Math.round;
import static net.minecraft.network.chat.Component.literal;

public class FullWidthSliderEntry extends AbstractEntry {

    private final Slider slider;

    protected FullWidthSliderEntry(Component caption, Tooltip tooltip, int minValue, int maxValue, int step, int initialValue, Consumer<Integer> onChange) {
        this.slider = new Slider(caption, minValue, maxValue, step, initialValue, onChange);
        this.slider.setTooltip(tooltip);
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.slider.setWidth(getContentWidth());
        this.slider.setPosition(getContentX(), getContentYMiddle() - this.slider.getHeight() / 2);
        this.slider.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.slider);
    }

    private class Slider extends AbstractSliderButton {

        private final Component caption;
        private final int minValue;
        private final int maxValue;
        private final int step;
        private final Consumer<Integer> onChange;

        public Slider(Component caption, int minValue, int maxValue, int step, int initialValue, Consumer<Integer> onChange) {
            double sliderValue = (double) (initialValue - minValue) / (maxValue - minValue);
            super(0, 0, FullWidthSliderEntry.this.getContentWidth(), DEFAULT_HEIGHT, caption, sliderValue);
            this.caption = caption;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.step = step;
            this.onChange = onChange;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            int mappedValue = toMappedValue(this.value);
            Component valuedCaption = this.caption.copy().append(literal(": " + mappedValue)).withStyle(this.caption.getStyle());
            setMessage(valuedCaption);
        }

        @Override
        protected void applyValue() {
            this.onChange.accept(toMappedValue(this.value));
        }

        private int toMappedValue(double value) {
            int raw = (int) round(this.minValue + (this.maxValue - this.minValue) * value);
            int snapped = this.minValue + round((raw - this.minValue) / (float) this.step) * this.step;
            return clamp(snapped, this.minValue, this.maxValue);
        }
    }
}
