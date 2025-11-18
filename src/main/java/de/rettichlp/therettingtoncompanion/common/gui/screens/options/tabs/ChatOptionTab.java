package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ButtonEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ChatRegexEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.SectionEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.ModOptionScreen;
import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.text.Text.literal;
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

        SectionEntry sectionEntry = new SectionEntry(translatable("trc.option.chat.section.message_patterns.title"));
        scrollableListEntries.add(sectionEntry);

        // default chat regex
        ChatRegex defaultChatRegex = new ChatRegex(this.client.getGameProfile().name(), configuration.isDefaultChatRegex(), 0);
        ChatRegexEntry defaultChatRegexEntry = new ChatRegexEntry(defaultChatRegex, false);
        scrollableListEntries.add(defaultChatRegexEntry);

        configuration.getChatRegexes().forEach(chatRegex -> {
            ChatRegexEntry chatRegexEntry = new ChatRegexEntry(chatRegex, true);
            scrollableListEntries.add(chatRegexEntry);
        });

        ButtonEntry buttonEntry = new ButtonEntry(literal("+"), button -> {
            ChatRegex newChatRegex = new ChatRegex("", true, 0);
            configuration.getChatRegexes().add(newChatRegex);
            this.client.execute(() -> this.client.setScreen(new ModOptionScreen("chat")));
        });
        scrollableListEntries.add(buttonEntry);

        return scrollableListEntries;
    }
}
