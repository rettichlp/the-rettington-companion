package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.ModOptionScreen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static net.minecraft.util.Formatting.YELLOW;

@Getter
@AllArgsConstructor
public abstract class AbstractOptionTab {

    protected final MinecraftClient client = MinecraftClient.getInstance();

    private final String id;

    public abstract Text displayName();

    public abstract Collection<ScrollableListEntry> getContent();

    public ButtonWidget getButton(boolean isActive) {
        Text displayName = isActive ? displayName().copy().formatted(YELLOW) : displayName();
        return ButtonWidget.builder(displayName, button -> this.client.setScreen(new ModOptionScreen(this.id)))
                .width(100)
                .build();
    }

    public ElementListWidget<ScrollableListEntry> createContentWidget(int width, int height, int x, int y) {
        return new OptionTabContent(width, height, x, y, getContent());
    }

    public static class OptionTabContent extends ElementListWidget<ScrollableListEntry> {

        public OptionTabContent(int width, int height, int x, int y, @NotNull Iterable<ScrollableListEntry> content) {
            super(MinecraftClient.getInstance(), width, height, y, 24);
            setPosition(x, y);
            content.forEach(this::addEntry);
        }

        @Override
        protected int getScrollbarX() {
            return this.width - 6;
        }

        @Override
        public int getRowLeft() {
            return 0;
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }
    }
}
