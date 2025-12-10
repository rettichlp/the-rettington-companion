package de.rettichlp.therettingtoncompanion.common.gui.screens.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

import static net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.text.Text.empty;

public class IconButtonWidget extends ButtonWidget {

    private final Identifier iconTexture;
    private final int iconU;
    private final int iconV;
    private final int iconWidth;
    private final int iconHeight;

    public IconButtonWidget(int x,
                            int y,
                            Identifier iconIdentifier,
                            int iconU,
                            int iconV,
                            int iconWidth,
                            int iconHeight,
                            PressAction onPress) {
        super(x, y, 20, 20, empty(), onPress, null);
        this.iconTexture = iconIdentifier;
        this.iconU = iconU;
        this.iconV = iconV;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);

        int iconX = getX() + (getWidth() - this.iconWidth) / 2;
        int iconY = getY() + (getHeight() - this.iconHeight) / 2;

        context.drawTexture(GUI_TEXTURED, this.iconTexture, iconX, iconY, this.iconU, this.iconV, this.iconWidth, this.iconHeight, this.iconWidth, this.iconHeight);
    }
}
