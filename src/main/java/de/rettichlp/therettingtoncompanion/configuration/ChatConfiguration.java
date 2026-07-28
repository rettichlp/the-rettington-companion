package de.rettichlp.therettingtoncompanion.configuration;

import de.rettichlp.therettingtoncompanion.models.ChatRegex;
import de.rettichlp.therettingtoncompanion.gui.options.list.HiddenMessageEntry;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
public class ChatConfiguration {

    @Accessors(fluent = true)
    private ChatRegexConfiguration regex = new ChatRegexConfiguration();
    private Set<HiddenMessageEntry.HiddenMessage> hiddenMessages = new HashSet<>();
    private boolean optimizedChatSize = true;
    private boolean moreMessages = true;
    private boolean keepMessagesOnDisconnect = true;
    private boolean chatTime = false;

    @Data
    public static class ChatRegexConfiguration {

        private ChatRegex defaulChatRegex = new ChatRegex("");
        private Set<ChatRegex> chatRegexes = new HashSet<>();
    }
}
