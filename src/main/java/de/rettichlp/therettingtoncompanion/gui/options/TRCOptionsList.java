package de.rettichlp.therettingtoncompanion.gui.options;

import de.rettichlp.therettingtoncompanion.gui.ICycleButtonValue;
import de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.OFF;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static java.lang.Math.max;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.minecraft.network.chat.Component.empty;

public class TRCOptionsList extends ContainerObjectSelectionList<TRCOptionsList.AbstractEntry> {

    private static final int ENTRY_HEIGHT = 24;
    private static final int ENTRY_HEADER_PADDING_TOP = Minecraft.getInstance().font.lineHeight * 2;

    private final TRCOptionsScreen screen;

    public TRCOptionsList(@NonNull Minecraft minecraft, @NonNull TRCOptionsScreen screen) {
        super(minecraft, screen.width, screen.layout.getContentHeight(), screen.layout.getHeaderHeight(), ENTRY_HEIGHT);
        this.centerListVertically = false;
        this.screen = screen;
    }

    @Override
    public int getRowWidth() {
        return max(424, this.screen.width / 3);
    }

    public void addHeader(Component text) {
        addEntry(new HeaderEntry(this.minecraft.font, text), ENTRY_HEIGHT + ENTRY_HEADER_PADDING_TOP);
    }

    public void addButton(Component label, Component caption, Button.OnPress onPress, Tooltip tooltip) {
        addEntry(new ButtonEntry(this.minecraft.font, label, caption, onPress, tooltip));
    }

    public <T extends ICycleButtonValue> void addCycleButton(Component label,
                                                             T initialValue,
                                                             Collection<T> values,
                                                             CycleButton.OnValueChange<T> valueChangeListener) {
        addEntry(new CycleButtonEntry<>(this.minecraft.font, label, initialValue, values, valueChangeListener));
    }

    public void addToggleButton(Component label,
                                boolean initialValue,
                                CycleButton.OnValueChange<OnOffCycleButtonEntry> valueChangeListener) {
        OnOffCycleButtonEntry initialMappedValue = initialValue ? ON : OFF;
        addEntry(new CycleButtonEntry<>(this.minecraft.font, label, initialMappedValue, asList(OnOffCycleButtonEntry.values()), valueChangeListener));
    }

    @NoArgsConstructor
    protected abstract static class AbstractEntry extends ContainerObjectSelectionList.Entry<AbstractEntry> {

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

        protected ButtonEntry(Font font, Component label, Component caption, Button.OnPress onPress, Tooltip tooltip) {
            this.stringWidget = new StringWidget(label, font);
            this.button = Button.builder(caption, onPress)
                    .size(100, 20)
                    .tooltip(tooltip)
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

    protected static class CycleButtonEntry<T extends ICycleButtonValue> extends AbstractEntry {

        private final StringWidget stringWidget;
        private final CycleButton<T> cycleButton;

        protected CycleButtonEntry(Font font,
                                   Component label,
                                   T initialValue,
                                   Collection<T> values,
                                   CycleButton.OnValueChange<T> valueChangeListener) {
            this.stringWidget = new StringWidget(label, font);
            this.cycleButton = CycleButton.builder(ICycleButtonValue::value, initialValue)
                    .withValues(values)
                    .withTooltip(ICycleButtonValue::tooltip)
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
}
