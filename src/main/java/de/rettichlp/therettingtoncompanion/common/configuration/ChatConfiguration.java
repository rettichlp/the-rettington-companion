package de.rettichlp.therettingtoncompanion.common.configuration;

import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.util.Formatting.GREEN;

@Data
public class ChatConfiguration {

    @Accessors(fluent = true)
    private ChatRegexConfiguration regex = new ChatRegexConfiguration();
    private boolean optimizedChatSize = true;

    @Data
    public static class ChatRegexConfiguration {

        private boolean defaultChatRegex = true;
        private Formatting defaultChatRegexColor = GREEN;
        private Set<ChatRegex> chatRegexes = new HashSet<>();
    }
}
