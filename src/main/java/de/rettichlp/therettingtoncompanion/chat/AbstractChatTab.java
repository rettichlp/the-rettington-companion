package de.rettichlp.therettingtoncompanion.chat;

import de.rettichlp.therettingtoncompanion.gui.ChatTabButton;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.clamp;
import static java.lang.Math.min;
import static java.lang.String.valueOf;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.TextColor.RED;

@Getter
@Setter
public abstract class AbstractChatTab {

    private transient List<GuiMessage> messages;
    private transient int unreadCount;
    private transient boolean filterTriggered;

    public List<GuiMessage> getMessages() {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }

        return this.messages;
    }

    public abstract @NonNull Component getDisplayName();

    public @NonNull ChatTabButton getChatTabButton(@NonNull Font font, Button.OnPress onPress) {
        return new ChatTabButton(font, this, onPress);
    }

    public @NonNull Component getButtonLabel() {
        MutableComponent label = empty().append(getDisplayName());

        if (this.unreadCount > 0) {
            String badgeText = valueOf(min(99, this.unreadCount));
            label.append(literal(" " + badgeText).withColor(RED));
        }

        return label;
    }

    public int getButtonWidth(@NonNull Font font) {
        return this instanceof AddChatTab ? 16 : clamp(font.width(getButtonLabel()) + 10, 20, 80);
    }

    public void clearUnreadState() {
        this.unreadCount = 0;
        this.filterTriggered = false;
    }
}
