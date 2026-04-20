package de.rettichlp.therettingtoncompanion.listener;

import de.rettichlp.therettingtoncompanion.common.gui.widgets.NotificationWidget;
import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.AbstractProgressTextWidget;
import de.rettichlp.therettingtoncompanion.common.registry.IHudRenderListener;
import de.rettichlp.therettingtoncompanion.common.registry.Listener;
import de.rettichlp.therettingtoncompanion.common.services.NotificationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.notificationService;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.renderService;
import static de.rettichlp.therettingtoncompanion.common.gui.widgets.base.AbstractWidget.Alignment.RIGHT;

@Listener
public class RenderListener implements IHudRenderListener {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        renderNotifications(drawContext);
        renderWidgets(drawContext);
    }

    private void renderNotifications(DrawContext drawContext) {
        ArrayList<AbstractProgressTextWidget<?>> widgets = new ArrayList<>();
        widgets.addAll(getNotificationWidgets());

        for (int i = 0; i < widgets.size(); i++) {
            AbstractProgressTextWidget<?> abstractUCUtilsProgressTextWidget = widgets.get(i);
            int x = MinecraftClient.getInstance().getWindow().getScaledWidth() - abstractUCUtilsProgressTextWidget.getWidth() - 4;
            int y = 19 * i + 4;
            abstractUCUtilsProgressTextWidget.draw(drawContext, x, y, RIGHT);
        }
    }

    private @NotNull @Unmodifiable List<NotificationWidget> getNotificationWidgets() {
        return notificationService.getActiveNotifications().stream()
                .map(NotificationService.Notification::toWidget)
                .toList();
    }

    private void renderWidgets(DrawContext drawContext) {
        renderService.getWidgets().forEach(ucUtilsWidgetInstance -> ucUtilsWidgetInstance.draw(drawContext));
    }
}
