package de.rettichlp.therettingtoncompanion.common.gui.screens.components.tab;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ButtonScrollableListEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import static net.fabricmc.api.EnvType.CLIENT;

@Environment(CLIENT)
public class TabMenuContentWidget extends TabMenuWidget {

    public TabMenuContentWidget(int width, int height, int x, int y) {
        super(width, height, x, y);
    }

    @Override
    public Collection<ScrollableListEntry> getContent() {
        return List.of(
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry(),
                new ButtonScrollableListEntry()
        );
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);

        context.drawVerticalLine(getX(), getY(), getY() + getHeight(), Color.BLUE.getRGB());
        context.drawVerticalLine(getX() + getWidth(), getY(), getY() + getHeight(), Color.BLUE.getRGB());
    }
}
