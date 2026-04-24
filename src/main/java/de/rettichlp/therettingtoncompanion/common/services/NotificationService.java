package de.rettichlp.therettingtoncompanion.common.services;

import de.rettichlp.therettingtoncompanion.common.gui.widgets.NotificationWidget;
import lombok.Data;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.renderService;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.time.LocalDateTime.now;
import static java.util.Objects.hash;
import static java.util.Objects.nonNull;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class NotificationService {

    private final Collection<Notification> notifications = new ArrayList<>();

    public void sendSuccessNotification(String message) {
        sendNotification(message, GREEN, 5000);
    }

    public void sendInfoNotification(String message) {
        sendNotification(message, CYAN, 5000);
    }

    public void sendWarningNotification(String message) {
        sendNotification(message, ORANGE, 5000);
    }

    public void sendErrorNotification(String message) {
        sendNotification(message, RED, 5000);
    }

    public void sendNotification(String message, Color color, long durationInMillis) {
        sendNotification(() -> Text.of(message), color, durationInMillis);
    }

    public void sendNotification(@NotNull Supplier<Text> messageSupplier, Color color, long durationInMillis) {
        Notification notification = new Notification(messageSupplier, durationInMillis);
        notification.setBorderColor(color);
        notification.setBackgroundColor(renderService.getSecondaryColor(color));
        this.notifications.add(notification);
    }

    public List<Notification> getActiveNotifications() {
        return this.notifications.stream()
                .filter(notification -> now().isBefore(notification.getTimestamp().plus(notification.getDurationInMillis(), MILLISECONDS.toChronoUnit())))
                .sorted(Comparator.comparing(Notification::getTimestamp))
                .toList();
    }
}
