package de.rettichlp.therettingtoncompanion.common.configuration;

import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_BELL;
import static net.minecraft.util.Formatting.GREEN;

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

        private ChatRegex defaulChatRegex = new ChatRegex("", BLOCK_NOTE_BLOCK_BELL.value().id(), true, GREEN, 0);
        private Set<ChatRegex> chatRegexes = new HashSet<>();
    }
}
