package de.rettichlp.therettingtoncompanion.chat;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

@Getter
@Setter
public abstract class AbstractChatTab {

    private transient int unreadCount;
    private transient boolean filterTriggered;

    public abstract @NonNull Component getDisplayName();
}
