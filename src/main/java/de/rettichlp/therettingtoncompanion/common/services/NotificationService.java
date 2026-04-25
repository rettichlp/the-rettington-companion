package de.rettichlp.therettingtoncompanion.common.services;

import de.rettichlp.therettingtoncompanion.common.models.Notification;
import lombok.Getter;
import net.minecraft.text.Text;

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

    public void sendSuccessNotification(Text text) {
        sendNotification(text, GREEN, 5000);
    }

    public void sendInfoNotification(Text text) {
        sendNotification(text, CYAN, 5000);
    }

    public void sendWarningNotification(Text text) {
        sendNotification(text, ORANGE, 5000);
    }

    public void sendErrorNotification(Text text) {
        sendNotification(text, RED, 5000);
    }

    public void sendNotification(Text text, Color color, long durationInMillis) {
        Notification notification = Notification.builder()
                .text(text)
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
