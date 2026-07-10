package de.rettichlp.therettingtoncompanion.gui.options.list;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList.ENTRY_HEADER_PADDING_TOP;
import static java.awt.Color.WHITE;
import static net.minecraft.ChatFormatting.BOLD;

public class HeaderEntry extends AbstractEntry {

    private final StringWidget stringWidget;

    protected HeaderEntry(Font font, @NonNull Component text) {
        this.stringWidget = new StringWidget(text.copy().withStyle(BOLD), font);
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.stringWidget.setPosition(getContentX(), getContentYMiddle() - this.stringWidget.getHeight() / 2 + ENTRY_HEADER_PADDING_TOP / 2);
        this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.horizontalLine(getContentX(), getContentRight(), getContentBottom(), WHITE.getRGB());
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.stringWidget);
    }
}
