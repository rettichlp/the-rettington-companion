package de.rettichlp.therettingtoncompanion.services;

import de.rettichlp.therettingtoncompanion.models.ChatLogEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getAllMessages;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.refreshTrimmedMessages;
import static de.rettichlp.therettingtoncompanion.utils.ModUtils.GSON;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static net.minecraft.network.chat.Component.translatable;

public class ChatLogService {

    private static final Path CHAT_LOG_PATH = FabricLoader.getInstance().getGameDir().resolve("chatlog.json");
    private static final GuiMessageTag LOADED_FROM_PREVIOUS_SESSION_TAG = new GuiMessageTag(-13474666, null, translatable("trc.chat_log.indicator"), "Previous session");

    private boolean chatLogLoaded = false;

    public void saveChatLog() {
        ChatLogEntry[] chatLogEntries = getAllMessages().stream()
                .map(guiMessage -> new ChatLogEntry(guiMessage.content(), guiMessage.source()))
                .toArray(ChatLogEntry[]::new);

        try (Writer writer = newBufferedWriter(CHAT_LOG_PATH)) {
            GSON.toJson(chatLogEntries, writer);
            LOGGER.info("Saved {} chat messages to {}", chatLogEntries.length, CHAT_LOG_PATH);
        } catch (Exception e) {
            LOGGER.error("Failed to save chat log to {}", CHAT_LOG_PATH, e);
        }
    }

    public void loadChatLogIfNeeded() {
        if (this.chatLogLoaded) {
            return;
        }

        this.chatLogLoaded = true;

        File file = CHAT_LOG_PATH.toFile();
        if (!file.exists() || file.length() == 0) {
            return;
        }

        try (Reader reader = newBufferedReader(CHAT_LOG_PATH)) {
            ChatLogEntry[] chatLogEntries = GSON.fromJson(reader, ChatLogEntry[].class);
            int guiTicks = Minecraft.getInstance().gui.hud.getGuiTicks();

            List<GuiMessage> loadedMessages = Arrays.stream(chatLogEntries)
                    .map(entry -> new GuiMessage(guiTicks, entry.getContent(), null, entry.getSource(), LOADED_FROM_PREVIOUS_SESSION_TAG))
                    .toList();

            getAllMessages().addAll(loadedMessages);
            refreshTrimmedMessages();

            LOGGER.info("Loaded {} chat messages from {}", loadedMessages.size(), CHAT_LOG_PATH);
        } catch (Exception e) {
            LOGGER.error("Failed to load chat log from {}", CHAT_LOG_PATH, e);
        }
    }
}
