package de.rettichlp.therettingtoncompanion.chat;

import de.rettichlp.therettingtoncompanion.utils.ChatUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.rettichlp.therettingtoncompanion.utils.ModUtils.getCurrentServerBaseDomain;
import static net.minecraft.network.chat.Component.literal;

@Getter
@Setter
public class CustomChatTab extends AbstractChatTab {

    private String name;
    private List<String> patternStrings = new ArrayList<>();

    private @Nullable String serverBoundDomain;

    private transient List<GuiMessage> messages;

    public CustomChatTab(String name) {
        this.name = name;
    }

    public List<GuiMessage> getMessages() {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }

        return this.messages;
    }

    @Override
    public @NonNull Component getDisplayName() {
        return literal(this.name == null || this.name.isBlank() ? "?" : this.name);
    }

    public boolean matches(@NonNull CharSequence message) {
        return this.patternStrings.stream()
                .map(ChatUtils::compiledPattern)
                .flatMap(Optional::stream)
                .anyMatch(pattern -> pattern.matcher(message).find());
    }

    /**
     * Whether this tab should be shown on the server the player is currently connected to. Always {@code true} unless
     * {@link #serverBoundDomain} is set and matches the current servers' domain.
     */
    public boolean isAvailableOnCurrentServer() {
        if (this.serverBoundDomain == null) {
            return true;
        }

        String currentServerDomain = getCurrentServerBaseDomain();
        return currentServerDomain != null && currentServerDomain.equalsIgnoreCase(this.serverBoundDomain);
    }
}
