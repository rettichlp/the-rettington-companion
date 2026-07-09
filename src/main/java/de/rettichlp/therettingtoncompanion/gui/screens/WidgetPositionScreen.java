package de.rettichlp.therettingtoncompanion.gui.screens;

import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.widgetService;
import static de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget.toNearestScale;
import static java.lang.Math.clamp;
import static net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.create;
import static net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner.INSTANCE;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

public class WidgetPositionScreen extends Screen {

    private final Screen lastScreen;

    private AbstractTRCWidget<?> selectedWidget;
    private double oldMouseX;
    private double oldMouseY;

    public WidgetPositionScreen(Screen lastScreen) {
        super(empty());
        this.lastScreen = lastScreen;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        if (this.selectedWidget != null) {
            FormattedCharSequence visualOrderText = literal("x: " + mouseX + " y: " + mouseY).getVisualOrderText();
            ClientTooltipComponent clientTooltipComponent = create(visualOrderText);
            graphics.tooltip(this.minecraft.font, List.of(clientTooltipComponent), mouseX, mouseY, INSTANCE, null);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void extractBlurredBackground(@NonNull GuiGraphicsExtractor graphics) {
        // don't blur background
    }

    @Override
    public void mouseMoved(double x, double y) {
        super.mouseMoved(x, y);

        double deltaX = x - this.oldMouseX;
        double deltaY = y - this.oldMouseY;

        this.oldMouseX = x;
        this.oldMouseY = y;

        if (this.selectedWidget != null) {
            WidgetConfiguration widgetConfiguration = this.selectedWidget.getWidgetConfiguration();
            double newX = clamp(widgetConfiguration.getX() + deltaX, 0, this.minecraft.getWindow().getGuiScaledWidth() - this.selectedWidget.getWidth());
            double newY = clamp(widgetConfiguration.getY() + deltaY, 0, this.minecraft.getWindow().getGuiScaledHeight() - this.selectedWidget.getHeight());
            widgetConfiguration.setX(newX);
            widgetConfiguration.setY(newY);
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        boolean mouseClicked = super.mouseClicked(event, doubleClick);

        widgetService.getInitializedWidgets().stream()
                .filter(abstractTRCWidget -> abstractTRCWidget.isMouseOver(event.x(), event.y()))
                .findFirst()
                .ifPresent(abstractTRCWidget -> {
                    abstractTRCWidget.setFocused(true);
                    this.selectedWidget = abstractTRCWidget;
                });

        return mouseClicked;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        boolean mouseReleased = super.mouseReleased(event);

        if (this.selectedWidget != null) {
            WidgetConfiguration widgetConfiguration = this.selectedWidget.getWidgetConfiguration();
            widgetConfiguration.setX(toNearestScale(this.minecraft, widgetConfiguration.getX()));
            widgetConfiguration.setY(toNearestScale(this.minecraft, widgetConfiguration.getY()));
            this.selectedWidget.setFocused(false);
        }

        this.selectedWidget = null;
        return mouseReleased;
    }
}
