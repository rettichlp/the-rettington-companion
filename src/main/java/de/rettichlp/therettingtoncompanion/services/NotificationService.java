package de.rettichlp.therettingtoncompanion.services;

import de.rettichlp.therettingtoncompanion.models.Notification;
import lombok.Getter;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.time.Duration.ofMillis;
import static java.util.Comparator.comparing;

public class NotificationService {

    @Getter
    private final Set<Notification> notifications = new HashSet<>();

    public void sendSuccessNotification(Component component) {
        sendNotification(component, GREEN, 5000);
    }

    public void sendInfoNotification(Component component) {
        sendNotification(component, CYAN, 5000);
    }

    public void sendWarningNotification(Component component) {
        sendNotification(component, ORANGE, 5000);
    }

    public void sendErrorNotification(Component component) {
        sendNotification(component, RED, 5000);
    }

    public void sendNotification(Component component, Color color, long durationInMillis) {
        Notification notification = Notification.builder()
                .text(component)
                .displayDuration(ofMillis(durationInMillis))
                .color(color)
                .build();

        this.notifications.add(notification);
    }

    public void sendNotification(Notification notification) {
        this.notifications.add(notification);
    }

    public List<Notification> getVisibleNotifications() {
        return this.notifications.stream()
                .filter(Notification::isVisible)
                .sorted(comparing(Notification::getTimestamp))
                .toList();
    }
}
