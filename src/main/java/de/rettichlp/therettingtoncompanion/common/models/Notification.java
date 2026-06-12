package de.rettichlp.therettingtoncompanion.common.models;

import lombok.Builder;
import lombok.Data;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static java.awt.Color.WHITE;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static java.util.Objects.hash;
import static java.util.UUID.randomUUID;

@Data
@Builder
public class Notification {

    private final UUID id = randomUUID();
    private final Component text;
    private final Duration displayDuration;
    @Builder.Default
    private LocalDateTime timestamp = now();
    @Builder.Default
    private Color color = WHITE;

    @Override
    public int hashCode() {
        return hash(this.id, this.text, this.displayDuration, this.timestamp, this.color);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Notification that && Objects.equals(this.id, that.id);
    }

    public boolean isVisible() {
        LocalDateTime now = now();
        return timestamp.isBefore(now) && between(this.timestamp, now).compareTo(this.displayDuration) < 0;
    }

//    public NotificationWidget toWidget() {
//        return new NotificationWidget(this);
//    }
}
