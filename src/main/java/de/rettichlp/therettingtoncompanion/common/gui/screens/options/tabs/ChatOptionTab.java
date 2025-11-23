package de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ButtonEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ChatRegexEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.SectionEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ToggleButtonEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.ModOptionScreen;
import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.GREEN;

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

        SectionEntry sectionEntry1 = new SectionEntry(translatable("trc.option.chat.section.general.title"));
        scrollableListEntries.add(sectionEntry1);

        ToggleButtonEntry toggleButtonEntry = new ToggleButtonEntry(translatable("trc.option.chat.optimized_chat_size.title"), translatable("trc.option.chat.optimized_chat_size.description"), configuration.chat().isOptimizedChatSize(), value -> configuration.chat().setOptimizedChatSize(value));
        scrollableListEntries.add(toggleButtonEntry);

        SectionEntry sectionEntry2 = new SectionEntry(translatable("trc.option.chat.section.message_patterns.title"));
        scrollableListEntries.add(sectionEntry2);

        // default chat regex
        ChatRegex defaultChatRegex = new ChatRegex(this.client.getGameProfile().name(), configuration.chat().regex().isDefaultChatRegex(), configuration.chat().regex().getDefaultChatRegexStyle(), 0);
        ChatRegexEntry defaultChatRegexEntry = new ChatRegexEntry(defaultChatRegex, false);
        scrollableListEntries.add(defaultChatRegexEntry);

        configuration.chat().regex().getChatRegexes().forEach(chatRegex -> {
            ChatRegexEntry chatRegexEntry = new ChatRegexEntry(chatRegex, true);
            scrollableListEntries.add(chatRegexEntry);
        });

        ButtonEntry buttonEntry = new ButtonEntry(literal("+"), button -> {
            ChatRegex newChatRegex = new ChatRegex("", true, GREEN, 0);
            configuration.chat().regex().getChatRegexes().add(newChatRegex);
            this.client.execute(() -> this.client.setScreen(new ModOptionScreen("chat")));
        });
        scrollableListEntries.add(buttonEntry);

        return scrollableListEntries;
    }
}
