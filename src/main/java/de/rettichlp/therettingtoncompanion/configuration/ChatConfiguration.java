package de.rettichlp.therettingtoncompanion.configuration;

import de.rettichlp.therettingtoncompanion.gui.options.list.FilteredMessageEntry;
import de.rettichlp.therettingtoncompanion.gui.options.list.HiddenMessageEntry;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
public class ChatConfiguration {

    @Accessors(fluent = true)
    private FilteredMessageConfiguration filteredMessage = new FilteredMessageConfiguration();
    private Set<HiddenMessageEntry.HiddenMessage> hiddenMessages = new HashSet<>();
    private boolean optimizedChat = true;
    private boolean moreMessages = true;
    private boolean keepMessagesOnDisconnect = true;
    private boolean chatTime = false;
    private boolean chatSearch = true;
    private boolean mergeDuplicateMessages = true;
    private boolean saveChatLog = true;

    @Data
    public static class FilteredMessageConfiguration {

        private FilteredMessageEntry.FilteredMessage defaultFilteredMessage = new FilteredMessageEntry.FilteredMessage("");
        private Set<FilteredMessageEntry.FilteredMessage> filteredMessages = new HashSet<>();
    }
}
