package de.rettichlp.therettingtoncompanion.gui.options.list;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList.ENTRY_HEADER_PADDING_TOP;
import static java.awt.Color.WHITE;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class HeaderEntry extends AbstractEntry {

    private final StringWidget stringWidget;
    private final @Nullable String iconPath;

    protected HeaderEntry(Font font, @NonNull Component text, @Nullable String iconPath) {
        this.stringWidget = new StringWidget(text.copy().withStyle(style -> style.withBold(true)), font);
        this.iconPath = iconPath;
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        if (this.iconPath != null) {
            graphics.blit(GUI_TEXTURED, fromNamespaceAndPath(this.iconPath, "icon.png"), getContentX(), getContentYMiddle() - 6 + ENTRY_HEADER_PADDING_TOP / 2, 0, 0, 12, 12, 12, 12);
        }

        this.stringWidget.setPosition(getContentX() + (this.iconPath != null ? 16 : 0), getContentYMiddle() - this.stringWidget.getHeight() / 2 + ENTRY_HEADER_PADDING_TOP / 2);
        this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.horizontalLine(getContentX(), getContentRight(), getContentBottom(), WHITE.getRGB());
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.stringWidget);
    }
}
