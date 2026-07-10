package de.rettichlp.therettingtoncompanion.gui.options.list;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class FullWidthButtonEntry extends AbstractEntry {

    @Getter
    private final Button button;

    protected FullWidthButtonEntry(Component caption, Tooltip tooltip, Button.OnPress onPress) {
        this.button = Button.builder(caption, onPress)
                .size(100, 20)
                .tooltip(tooltip)
                .build();
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.button.setWidth(getContentWidth());
        this.button.setPosition(getContentX(), getContentYMiddle() - this.button.getHeight() / 2);
        this.button.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.button);
    }
}
