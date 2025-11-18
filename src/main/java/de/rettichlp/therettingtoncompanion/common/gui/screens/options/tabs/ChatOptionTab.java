package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ChatRegexEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static java.util.regex.Pattern.compile;
import static net.minecraft.text.Text.translatable;

public class ChatOptionTab extends AbstractOptionTab {

    public ChatOptionTab() {
        super("chat");
    }

    @Override
    public Text displayName() {
        return translatable("trc.option.chat.title");
    }

    @Override
    public Collection<ScrollableListEntry> getContent() {
        Collection<ScrollableListEntry> scrollableListEntries = new ArrayList<>();

        // default chat regex
        ChatRegex defaultChatRegex = new ChatRegex(compile(this.client.getGameProfile().name()), configuration.isDefaultChatRegex(), 0);
        ChatRegexEntry defaultChatRegexEntry = new ChatRegexEntry(defaultChatRegex, false);
        scrollableListEntries.add(defaultChatRegexEntry);

        configuration.getChatRegexes().forEach(chatRegex -> {
            ChatRegexEntry chatRegexEntry = new ChatRegexEntry(chatRegex, true);
            scrollableListEntries.add(chatRegexEntry);
        });

        return scrollableListEntries;
    }
}
