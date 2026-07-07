package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen;
import de.rettichlp.therettingtoncompanion.models.ChatRegex;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

public class ChatOptionsTab extends AbstractTRCOptionsTab {

    public ChatOptionsTab() {
        super("chat");
    }

    @Override
    public Component title() {
        return translatable("trc.option.chat.title");
    }

    @Override
    public void populateOptionsList(@NonNull TRCOptionsList optionsList) {
        optionsList.addHeader(translatable("trc.option.chat.section.general.title"));
        optionsList.addToggleButton(translatable("trc.option.chat.optimized_chat_size.label"), create(translatable("trc.option.chat.optimized_chat_size.tooltip")), configuration.chat().isOptimizedChatSize(), (_, value) -> configuration.chat().setOptimizedChatSize(value == ON));
        optionsList.addToggleButton(translatable("trc.option.chat.more_messages.label"), create(translatable("trc.option.chat.more_messages.tooltip")), configuration.chat().isMoreMessages(), (_, value) -> configuration.chat().setMoreMessages(value == ON));
        optionsList.addToggleButton(translatable("trc.option.chat.keep_messages_on_disconnect.label"), create(translatable("trc.option.chat.keep_messages_on_disconnect.tooltip")), configuration.chat().isKeepMessagesOnDisconnect(), (_, value) -> configuration.chat().setKeepMessagesOnDisconnect(value == ON));
        optionsList.addToggleButton(translatable("trc.option.chat.chat_time.label"), create(translatable("trc.option.chat.chat_time.tooltip")), configuration.chat().isChatTime(), (_, value) -> configuration.chat().setChatTime(value == ON));

        optionsList.addHeader(translatable("trc.option.chat.section.message_patterns.title"));
        optionsList.addChatRegexEntry(configuration.chat().regex().getDefaulChatRegex(), false);
        configuration.chat().regex().getChatRegexes().forEach(chatRegex -> optionsList.addChatRegexEntry(chatRegex, true));
        optionsList.addFullWidthButton(literal("+"), create(empty()), _ -> {
            ChatRegex newChatRegex = new ChatRegex("");
            configuration.chat().regex().getChatRegexes().add(newChatRegex);
            this.minecraft.setScreen(new TRCOptionsScreen("chat", new PauseScreen(true), true));
        });
    }
}
