package de.rettichlp.therettingtoncompanion.common.configuration;

import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import lombok.Data;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.util.Formatting.GREEN;

@Data
public class ChatConfiguration {

    private boolean optimizedChatSize = true;
    private ChatRegexConfiguration chatRegexConfiguration = new ChatRegexConfiguration();

    @Data
    public static class ChatRegexConfiguration {

        private boolean defaultChatRegex = true;
        private Formatting defaultChatRegexStyle = GREEN;
        private Set<ChatRegex> chatRegexes = new HashSet<>();
    }
}
