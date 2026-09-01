package de.rettichlp.therettingtoncompanion.configuration;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.compiledPattern;
import static de.rettichlp.therettingtoncompanion.utils.ModUtils.getCurrentServerBaseDomain;

@Getter
@Setter
public class ChatTab {

    private String name;
    private List<String> patternStrings = new ArrayList<>();

    private @Nullable String serverBoundDomain;

    private transient int unreadCount;
    private transient boolean filterTriggered;

    public ChatTab(String name) {
        this.name = name;
    }

    public boolean matches(@NonNull CharSequence message) {
        for (String patternString : this.patternStrings) {
            Pattern pattern = compiledPattern(patternString).orElse(null);
            if (pattern != null && pattern.matcher(message).find()) {
                return true;
            }
        }

        return false;
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
