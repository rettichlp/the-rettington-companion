package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.TheRettingtonCompanionApi;
import de.rettichlp.therettingtoncompanion.gui.options.list.FilteredMessageEntry;
import de.rettichlp.therettingtoncompanion.gui.options.list.HiddenMessageEntry;
import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
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
        optionsList.addHeader(translatable("trc.option.chat.general.section_title"));
        optionsList.addToggleButton(translatable("trc.option.chat.optimized_chat.label"), create(translatable("trc.option.chat.optimized_chat.tooltip")), configuration.chat().isOptimizedChat(), (_, value) -> configuration.chat().setOptimizedChat(value == ON));
        optionsList.addToggleButton(translatable("trc.option.chat.general.more_messages.label"), create(translatable("trc.option.chat.general.more_messages.tooltip")), configuration.chat().isMoreMessages(), (_, value) -> configuration.chat().setMoreMessages(value == ON));
        optionsList.addToggleButton(translatable("trc.option.chat.general.keep_messages_on_disconnect.label"), create(translatable("trc.option.chat.general.keep_messages_on_disconnect.tooltip")), configuration.chat().isKeepMessagesOnDisconnect(), (_, value) -> configuration.chat().setKeepMessagesOnDisconnect(value == ON));
        optionsList.addToggleButton(translatable("trc.option.chat.general.chat_time.label"), create(translatable("trc.option.chat.general.chat_time.tooltip")), configuration.chat().isChatTime(), (_, value) -> configuration.chat().setChatTime(value == ON));

        optionsList.addHeader(translatable("trc.option.chat.filtered_messages.section_title"));
        optionsList.addFilteredMessageEntry(configuration.chat().filteredMessage().getDefaultFilteredMessage(), false);
        configuration.chat().filteredMessage().getFilteredMessages().forEach(filteredMessage -> optionsList.addFilteredMessageEntry(filteredMessage, true));
        optionsList.addFullWidthButton(literal("+"), create(empty()), _ -> {
            FilteredMessageEntry.FilteredMessage newFilteredMessage = new FilteredMessageEntry.FilteredMessage("");
            configuration.chat().filteredMessage().getFilteredMessages().add(newFilteredMessage);
            this.minecraft.gui.setScreen(new TRCOptionsScreen("chat", new PauseScreen(true), true));
        });

        optionsList.addHeader(translatable("trc.option.chat.hidden_messages.section_title"));
        configuration.chat().getHiddenMessages().forEach(optionsList::addHiddenMessageEntry);
        // load hidden messages from other mods
        FabricLoader.getInstance().getEntrypointContainers(MOD_ID, TheRettingtonCompanionApi.class).forEach(container -> {
            TheRettingtonCompanionApi entrypoint = container.getEntrypoint();
            entrypoint.getHiddenMessages().stream()
                    .filter(hiddenMessage -> !hiddenMessage.getProviderModId().isBlank())
                    .forEach(optionsList::addHiddenMessageEntry);
        });
        optionsList.addFullWidthButton(literal("+"), create(empty()), _ -> {
            HiddenMessageEntry.HiddenMessage newHiddenMessage = new HiddenMessageEntry.HiddenMessage("");
            configuration.chat().getHiddenMessages().add(newHiddenMessage);
            this.minecraft.gui.setScreen(new TRCOptionsScreen("chat", new PauseScreen(true), true));
        });
    }
}
