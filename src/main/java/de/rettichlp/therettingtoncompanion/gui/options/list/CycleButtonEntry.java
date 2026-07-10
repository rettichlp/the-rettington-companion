package de.rettichlp.therettingtoncompanion.gui.options.list;

import de.rettichlp.therettingtoncompanion.gui.ICycleButtonValue;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

import static net.minecraft.network.chat.Component.empty;

public class CycleButtonEntry<T extends ICycleButtonValue> extends AbstractEntry {

    private final StringWidget stringWidget;
    private final CycleButton<T> cycleButton;

    protected CycleButtonEntry(Font font,
                               Component label,
                               Tooltip tooltip,
                               T initialValue,
                               Collection<T> values,
                               CycleButton.OnValueChange<T> valueChangeListener) {
        this.stringWidget = new StringWidget(label, font);
        this.stringWidget.setTooltip(tooltip);
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

        this.cycleButton.setPosition(getContentRight() - this.cycleButton.getWidth() + 2, getContentYMiddle() - this.cycleButton.getHeight() / 2);
        this.cycleButton.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.stringWidget, this.cycleButton);
    }
}
