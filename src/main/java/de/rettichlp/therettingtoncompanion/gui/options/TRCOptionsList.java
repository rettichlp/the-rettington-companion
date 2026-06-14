package de.rettichlp.therettingtoncompanion.gui.options;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static net.minecraft.client.gui.components.CycleButton.onOffBuilder;
import static net.minecraft.network.chat.Component.empty;

public class TRCOptionsList extends ContainerObjectSelectionList<TRCOptionsList.AbstractEntry> {

    private static final int ENTRY_HEIGHT = 24;
    private static final int ENTRY_WIDTH = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
    private static final int ENTRY_HEADER_PADDING_TOP = Minecraft.getInstance().font.lineHeight * 2;

    public TRCOptionsList(@NonNull Minecraft minecraft, @NonNull TRCOptionsScreen screen) {
        super(minecraft, ENTRY_WIDTH, screen.layout.getContentHeight(), screen.layout.getHeaderHeight(), ENTRY_HEIGHT);
        this.centerListVertically = false;
    }

    public void addHeader(Component text) {
        addEntry(new HeaderEntry(this.minecraft.font, text), ENTRY_HEIGHT + ENTRY_HEADER_PADDING_TOP);
    }

    public void addButton(Component label, Component caption, Button.OnPress onPress) {
        addEntry(new ButtonEntry(this.minecraft.font, label, caption, onPress));
    }

    public <T> void addCycleButton(Component label,
                                   T initialValue,
                                   Collection<T> values,
                                   Function<T, Component> valueStringifier,
                                   CycleButton.OnValueChange<T> valueChangeListener) {
        addEntry(new CycleButtonEntry<>(this.minecraft.font, label, initialValue, values, valueStringifier, valueChangeListener));
    }

    public void addToggleButton(Component label, boolean initialValue, CycleButton.OnValueChange<Boolean> valueChangeListener) {
        addEntry(new ToggleButtonEntry(this.minecraft.font, label, initialValue, valueChangeListener));
    }

    protected abstract static class AbstractEntry extends ContainerObjectSelectionList.Entry<AbstractEntry> {

        protected AbstractEntry() {
            setX((Minecraft.getInstance().getWindow().getGuiScaledWidth() - ENTRY_WIDTH) / 2);
            setWidth(ENTRY_WIDTH);
        }

        @Override
        public @NonNull List<? extends NarratableEntry> narratables() {
            return emptyList();
        }
    }

    protected static class HeaderEntry extends AbstractEntry {

        private final StringWidget stringWidget;

        protected HeaderEntry(Font font, Component text) {
            this.stringWidget = new StringWidget(text, font);
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            this.stringWidget.setPosition(getContentX(), getContentYMiddle() - this.stringWidget.getHeight() / 2 + ENTRY_HEADER_PADDING_TOP / 2);
            this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public @NonNull List<? extends GuiEventListener> children() {
            return List.of(this.stringWidget);
        }
    }

    protected static class ButtonEntry extends AbstractEntry {

        private final StringWidget stringWidget;
        private final Button button;

        protected ButtonEntry(Font font, Component label, Component caption, Button.OnPress onPress) {
            this.stringWidget = new StringWidget(label, font);
            this.button = Button.builder(caption, onPress)
                    .size(100, 20)
                    .build();
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            this.stringWidget.setPosition(getContentX(), getContentYMiddle() - this.stringWidget.getHeight() / 2);
            this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);

            this.button.setPosition(getContentRight() - this.button.getWidth(), getContentYMiddle() - this.button.getHeight() / 2);
            this.button.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public @NonNull List<? extends GuiEventListener> children() {
            return List.of(this.stringWidget, this.button);
        }
    }

    protected static class CycleButtonEntry<T> extends AbstractEntry {

        protected CycleButton<T> cycleButton;

        private final StringWidget stringWidget;

        protected CycleButtonEntry(Font font,
                                   Component label,
                                   T initialValue,
                                   Collection<T> values,
                                   Function<T, Component> valueStringifier,
                                   CycleButton.OnValueChange<T> valueChangeListener) {
            this.stringWidget = new StringWidget(label, font);
            this.cycleButton = CycleButton.builder(valueStringifier, initialValue)
                    .withValues(values)
                    .displayOnlyValue()
                    .create(0, 0, 100, 20, empty(), valueChangeListener);
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            this.stringWidget.setPosition(getContentX(), getContentYMiddle() - this.stringWidget.getHeight() / 2);
            this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);

            this.cycleButton.setPosition(getContentRight() - this.cycleButton.getWidth(), getContentYMiddle() - this.cycleButton.getHeight() / 2);
            this.cycleButton.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public @NonNull List<? extends GuiEventListener> children() {
            return List.of(this.stringWidget, this.cycleButton);
        }
    }

    protected static class ToggleButtonEntry extends CycleButtonEntry<Boolean> {

        protected ToggleButtonEntry(Font font,
                                    Component label,
                                    boolean initialValue,
                                    CycleButton.OnValueChange<Boolean> valueChangeListener) {
            super(font, label, initialValue, null, null, valueChangeListener);
            this.cycleButton = onOffBuilder(initialValue)
                    .displayOnlyValue()
                    .create(0, 0, 100, 20, empty(), valueChangeListener);
        }
    }
}
