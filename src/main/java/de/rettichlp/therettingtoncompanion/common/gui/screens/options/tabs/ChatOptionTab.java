package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ChatRegexEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
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

        configuration.getChatRegexes().forEach(chatRegex -> {
            ChatRegexEntry chatRegexEntry = new ChatRegexEntry(chatRegex);
            scrollableListEntries.add(chatRegexEntry);
        });

        return scrollableListEntries;
    }
}
