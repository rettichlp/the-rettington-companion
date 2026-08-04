package de.rettichlp.therettingtoncompanion.gui.widgets;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCProgressTextWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidgetGroup;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AlwaysEnabled;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import de.rettichlp.therettingtoncompanion.models.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.Color;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.notificationService;
import static de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidgetGroup.Alignment.LEFT;
import static de.rettichlp.therettingtoncompanion.services.WidgetService.calculateProgress;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.translatable;

@AlwaysEnabled
public class NotificationGroupWidget extends AbstractTRCWidgetGroup<NotificationGroupWidget.Configuration> {

    @Override
    public @Nullable String getRegistryName() {
        return "notification_group";
    }

    @Override
    public Component getLabel() {
        return translatable("trc.widgets.notification_group.label");
    }

    @Override
    public Component getTooltip() {
        return translatable("trc.widgets.notification_group.tooltip");
    }

    @Override
    public void addOptions(@NonNull TRCOptionsList optionsList) {
        optionsList.addCycleButton(translatable("trc.widgets.notification_group.options.alignment.label"), create(translatable("trc.widgets.notification_group.options.alignment.tooltip")), getWidgetConfiguration().getAlignment(), List.of(Alignment.values()), (_, value) -> getWidgetConfiguration().setAlignment(value));
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void extractWidget(@NotNull GuiGraphicsExtractor graphics, int x, int y, Color color, boolean backgroundEnabled) {
        super.extractWidget(graphics, x, y, color, backgroundEnabled);

        if (isWidgetPositionScreen()) {
            // render caption to tell users what this blue box is
            MutableComponent label = translatable("trc.widgets.notification_group.label");
            graphics.text(this.minecraft.font, label, x + getWidth() / 2 - this.minecraft.font.width(label) / 2, y + getHeight() / 2 - this.minecraft.font.lineHeight / 2, color.getRGB(), false);
        }
    }

    @Override
    public List<? extends AbstractTRCWidget<?>> widgets() {
        return notificationService.getVisibleNotifications().stream()
                .map(NotificationWidget::new)
                .toList();
    }

    @Override
    public Alignment alignment() {
        return getWidgetConfiguration().getAlignment();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Configuration extends WidgetConfiguration {

        private Alignment alignment = LEFT;
    }

    @Getter
    @AllArgsConstructor
    private static class NotificationWidget extends AbstractTRCProgressTextWidget<NotificationGroupWidget.Configuration> {

        private final Notification notification;

        @Override
        public Component text() {
            return this.notification.getComponentSupplier().get();
        }

        @Override
        public double progress() {
            return calculateProgress(this.notification.getTimestamp(), this.notification.getDisplayDuration().toMillis());
        }

        @Contract(pure = true)
        @Override
        public @Nullable String getRegistryName() {
            return null;
        }

        @Contract(value = " -> new", pure = true)
        @Override
        public @NonNull Component getLabel() {
            return empty();
        }

        @Contract(value = " -> new", pure = true)
        @Override
        public @NonNull Component getTooltip() {
            return empty();
        }

        @Override
        public void addOptions(@NonNull TRCOptionsList optionsList) {}
    }
}
