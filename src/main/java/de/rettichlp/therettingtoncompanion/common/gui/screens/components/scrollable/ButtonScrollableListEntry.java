package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable;

import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import static net.fabricmc.api.EnvType.CLIENT;

@Environment(CLIENT)
public class ButtonScrollableListEntry extends ScrollableListEntry {

    private final ButtonWidget buttonWidget;

    public ButtonScrollableListEntry() {
        this.buttonWidget = ButtonWidget.builder(Text.literal("test b"), button -> System.out.println("button"))
                .build();

        this.children.add(this.buttonWidget);
    }

    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.buttonWidget.setWidth(getContentMiddleX() - 8); // 4 pixels padding on each side
        this.buttonWidget.setX(getContentMiddleX() - (this.buttonWidget.getWidth() / 2));
        this.buttonWidget.setY(getContentY());
        this.buttonWidget.render(context, mouseX, mouseY, deltaTicks);
    }
}
