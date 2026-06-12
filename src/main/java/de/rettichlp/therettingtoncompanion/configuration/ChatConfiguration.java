package de.rettichlp.therettingtoncompanion.configuration;

import de.rettichlp.therettingtoncompanion.models.ChatRegex;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

import static java.awt.Color.GREEN;
import static net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL;

@Data
public class ChatConfiguration {

    @Accessors(fluent = true)
    private ChatRegexConfiguration regex = new ChatRegexConfiguration();
    private boolean optimizedChatSize = true;
    private boolean moreMessages = true;
    private boolean keepMessagesOnDisconnect = true;
    private boolean chatTime = false;

    @Data
    public static class ChatRegexConfiguration {

        private ChatRegex defaulChatRegex = new ChatRegex("", NOTE_BLOCK_BELL.value().location(), true, GREEN, 0);
        private Set<ChatRegex> chatRegexes = new HashSet<>();
    }
}
