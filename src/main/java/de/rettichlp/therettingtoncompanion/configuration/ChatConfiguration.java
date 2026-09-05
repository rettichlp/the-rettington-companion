package de.rettichlp.therettingtoncompanion.configuration;

import de.rettichlp.therettingtoncompanion.chat.CustomChatTab;
import de.rettichlp.therettingtoncompanion.gui.options.list.FilteredMessageEntry;
import de.rettichlp.therettingtoncompanion.gui.options.list.HiddenMessageEntry;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ChatConfiguration {

    private static final int DEFAULT_MAX_CHAT_MESSAGES = 100;

    @Accessors(fluent = true)
    private FilteredMessageConfiguration filteredMessage = new FilteredMessageConfiguration();
    private Set<HiddenMessageEntry.HiddenMessage> hiddenMessages = new HashSet<>();
    private List<CustomChatTab> chatTabs = new ArrayList<>();
    private boolean optimizedChat = true;
    private int maxChatMessages = 5000;
    private boolean keepMessagesOnDisconnect = true;
    private boolean chatTime = false;
    private boolean chatSearch = true;
    private boolean mergeDuplicateMessages = true;
    private boolean saveChatLog = true;

    public int getEffectiveMaxChatMessages() {
        return this.maxChatMessages == 0 ? DEFAULT_MAX_CHAT_MESSAGES : this.maxChatMessages;
    }

    @Data
    public static class FilteredMessageConfiguration {

        private FilteredMessageEntry.FilteredMessage defaultFilteredMessage = new FilteredMessageEntry.FilteredMessage("");
        private Set<FilteredMessageEntry.FilteredMessage> filteredMessages = new HashSet<>();
    }
}
