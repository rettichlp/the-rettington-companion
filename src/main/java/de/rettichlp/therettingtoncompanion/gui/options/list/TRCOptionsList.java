package de.rettichlp.therettingtoncompanion.gui.options.list;

import de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration;
import de.rettichlp.therettingtoncompanion.gui.ColorButton;
import de.rettichlp.therettingtoncompanion.gui.ICycleButtonValue;
import de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry;
import de.rettichlp.therettingtoncompanion.gui.options.TRCOptionsScreen;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.OFF;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static java.lang.Math.max;
import static java.util.Arrays.asList;

public class TRCOptionsList extends ContainerObjectSelectionList<AbstractEntry> {

    public static final int ENTRY_HEADER_PADDING_TOP = Minecraft.getInstance().font.lineHeight * 2;

    private static final int ENTRY_HEIGHT = 24;

    @Getter
    private final TRCOptionsScreen screen;

    public TRCOptionsList(@NonNull Minecraft minecraft, @NonNull TRCOptionsScreen screen) {
        super(minecraft, screen.width, screen.getLayout().getContentHeight(), screen.getLayout().getHeaderHeight(), ENTRY_HEIGHT);
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

    public void addButton(Component label, Component caption, Tooltip tooltip, Button.OnPress onPress) {
        addEntry(new ButtonEntry(this.minecraft.font, label, caption, tooltip, onPress));
    }

    public <T extends ICycleButtonValue> void addCycleButton(Component label,
                                                             Tooltip tooltip,
                                                             T initialValue,
                                                             Collection<T> values,
                                                             CycleButton.OnValueChange<T> valueChangeListener) {
        addEntry(new CycleButtonEntry<>(this.minecraft.font, label, tooltip, initialValue, values, valueChangeListener));
    }

    public void addToggleButton(Component label,
                                Tooltip tooltip,
                                boolean initialValue,
                                CycleButton.OnValueChange<OnOffCycleButtonEntry> valueChangeListener) {
        OnOffCycleButtonEntry initialMappedValue = initialValue ? ON : OFF;
        addEntry(new CycleButtonEntry<>(this.minecraft.font, label, tooltip, initialMappedValue, asList(OnOffCycleButtonEntry.values()), valueChangeListener));
    }

    public void addWeatherButton(Component label, Tooltip tooltip, Consumer<VisualsConfiguration.WeatherValue> valueChangeListener) {
        addEntry(new WeatherButtonEntry(this.minecraft.font, label, tooltip, valueChangeListener));
    }

    public void addColorButton(Component label, Tooltip tooltip, int initialValue, BiConsumer<ColorButton, Integer> onPress) {
        addEntry(new ColorButtonEntry(this.minecraft.font, label, tooltip, initialValue, onPress));
    }
}
