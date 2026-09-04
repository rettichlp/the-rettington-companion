package de.rettichlp.therettingtoncompanion.utils;

import com.mojang.blaze3d.platform.Window;
import de.rettichlp.therettingtoncompanion.chat.AbstractChatTab;
import de.rettichlp.therettingtoncompanion.chat.AddChatTab;
import de.rettichlp.therettingtoncompanion.chat.CustomChatTab;
import de.rettichlp.therettingtoncompanion.chat.DefaultChatTab;
import de.rettichlp.therettingtoncompanion.gui.ChatTabButton;
import de.rettichlp.therettingtoncompanion.gui.options.list.FilteredMessageEntry.FilteredMessage;
import de.rettichlp.therettingtoncompanion.mixin.ChatComponentAccessor;
import de.rettichlp.therettingtoncompanion.models.ChatLogEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.gui.options.list.FilteredMessageEntry.FilteredMessage.getBestMatchingFilteredMessage;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.unmodifiableMap;
import static java.util.Comparator.comparingLong;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static net.minecraft.client.gui.components.ChatComponent.getHeight;
import static net.minecraft.client.gui.components.ChatComponent.getWidth;
import static net.minecraft.util.Mth.ceil;
import static net.minecraft.util.Mth.floor;
import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;

public class ChatUtils {

    public static final List<ChatTabButton> CHAT_TAB_BUTTONS = new ArrayList<>();
    public static final DefaultChatTab DEFAULT_CHAT_TAB = new DefaultChatTab();
    public static final AddChatTab ADD_CHAT_TAB = new AddChatTab();

    public static AbstractChatTab FOCUSED_CHAT_TAB = DEFAULT_CHAT_TAB;

    public static @NonNull List<AbstractChatTab> getAllChatTabs() {
        List<AbstractChatTab> allChatTabs = new ArrayList<>();
        allChatTabs.add(DEFAULT_CHAT_TAB);
        configuration.chat().getChatTabs().stream()
                .filter(CustomChatTab::isAvailableOnCurrentServer)
                .forEach(allChatTabs::add);
        return allChatTabs;
    }

    // identity map is required here (two messages with equal content and source would otherwise collide as the same map key)
    private static final Map<GuiMessage, MessageMeta> MESSAGE_CACHE = new IdentityHashMap<>();
    // regex compilation is comparatively expensive, and these patterns (chat tabs, filtered/hidden messages) are matched against every
    // chat message as it's classified, so recompiling the same pattern string on every single match call causes noticeable lag
    private static final Map<String, Optional<Pattern>> COMPILED_PATTERN_CACHE = new HashMap<>();
    private static final Comparator<Map.Entry<GuiMessage, MessageMeta>> BY_TIMESTAMP = comparingLong(entry -> entry.getValue().receivedAt());

    public static double getMaxChatWidth(Window window, double defaultChatWidth) {
        return !configuration.chat().isOptimizedChat()
                ? defaultChatWidth
                : window.getGuiScaledWidth() / 2.0 - 40 - 12; // minus min width; I don't know from where the offset of 12 comes
    }

    public static double getMaxChatHeight(Window window, double defaultChatHeight) {
        return !configuration.chat().isOptimizedChat()
                ? defaultChatHeight
                : window.getGuiScaledHeight() / 2.0 - 20; // minus min height
    }

    public static int getChatBottomHeight() {
        Minecraft minecraft = Minecraft.getInstance();

        if (!configuration.chat().isOptimizedChat()) {
            return floor((minecraft.getWindow().getGuiScaledHeight() - 40) / minecraft.options.chatScale().get());
        }

        int yLineBase = minecraft.getWindow().getGuiScaledHeight() - 39;
        float maxHealth = max((float) player.getAttributeValue(MAX_HEALTH), player.getHealth());
        int totalAbsorption = ceil(player.getAbsorptionAmount());
        int numHealthRows = ceil((maxHealth + (float) totalAbsorption) / 2.0F / 10.0F);
        int healthRowHeight = max(10 - (numHealthRows - 2), 3);
        int yLineArmor = yLineBase - (numHealthRows - 1) * healthRowHeight - 10;
        int yLineChatBottom = player.getArmorValue() > 0 ? yLineArmor : yLineArmor + 10;
        return min(minecraft.getWindow().getGuiScaledHeight() - 47, yLineChatBottom); // same height as empty inventory slot count
    }

    public static int getChatTopHeight() {
        Options options = Minecraft.getInstance().options;
        int lineHeight = 9;
        int maxVisibleLines = getHeight(options.chatHeightFocused().get()) / lineHeight;
        int visibleLines = min(getTrimmedMessages().size(), maxVisibleLines);
        return getChatBottomHeight() - visibleLines * lineHeight;
    }

    public static int getChatLeft() {
        return 2; // 2 because indicator offset
    }

    public static int getChatRight() {
        Options options = Minecraft.getInstance().options;
        return getWidth(options.chatWidth().get()) + 12; // I don't know from where the offset of 12 comes
    }

    public static boolean isMessageVisible(@NonNull GuiMessage guiMessage) {
        MessageMeta messageMeta = getMessageMeta(guiMessage);
        if (messageMeta == null) {
            return true;
        }

        return FOCUSED_CHAT_TAB instanceof CustomChatTab customChatTab
                ? messageMeta.matchingChatTabs().contains(customChatTab)
                : messageMeta.matchingChatTabs().isEmpty();
    }

    public static @Nullable MessageMeta getMessageMeta(@NonNull GuiMessage message) {
        return MESSAGE_CACHE.get(message);
    }

    public static @Nullable GuiMessage getMostRecentMessage() {
        return MESSAGE_CACHE.entrySet().stream()
                .max(BY_TIMESTAMP)
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static @NonNull @Unmodifiable Map<GuiMessage, MessageMeta> getMessages() {
        Map<GuiMessage, MessageMeta> sorted = MESSAGE_CACHE.entrySet().stream()
                .sorted(BY_TIMESTAMP)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
        return unmodifiableMap(sorted);
    }

    public static void unregisterMessage(@NonNull GuiMessage message) {
        MessageMeta messageMeta = MESSAGE_CACHE.remove(message);
        if (messageMeta == null) {
            return;
        }

        if (messageMeta.matchingChatTabs().isEmpty()) {
            DEFAULT_CHAT_TAB.getMessages().removeIf(m -> m == message);
        } else {
            messageMeta.matchingChatTabs().forEach(chatTab -> chatTab.getMessages().removeIf(m -> m == message));
        }

        getAllMessages().removeIf(m -> m == message);
        getTrimmedMessages().removeIf(line -> line.parent() == message);
    }

    public static void rebuildMessageClassification() {
        Map<GuiMessage, MessageMeta> messages = getMessages();

        MESSAGE_CACHE.clear();
        DEFAULT_CHAT_TAB.getMessages().clear();
        configuration.chat().getChatTabs().forEach(chatTab -> chatTab.getMessages().clear());

        messages.forEach((message, messageMeta) -> registerMessage(message, messageMeta.receivedAt(), false));

        applyFocusedChatTabMessages();
    }

    public static void registerMessage(@NonNull GuiMessage message, @Nullable Long timestamp, boolean live) {
        // classify the message
        String messageString = message.content().getString();
        long receivedAt = timestamp != null ? timestamp : currentTimeMillis();
        Set<CustomChatTab> matchingChatTabs = configuration.chat().getChatTabs().stream()
                .filter(CustomChatTab::isAvailableOnCurrentServer)
                .filter(chatTab -> chatTab.matches(messageString))
                .collect(toUnmodifiableSet());
        FilteredMessage bestMatchingFilteredMessage = getBestMatchingFilteredMessage(messageString);

        MessageMeta messageMeta = new MessageMeta(receivedAt, matchingChatTabs, bestMatchingFilteredMessage);
        MESSAGE_CACHE.put(message, messageMeta);

        // add message to chat tabs
        if (matchingChatTabs.isEmpty()) {
            DEFAULT_CHAT_TAB.getMessages().addFirst(message);
        } else {
            matchingChatTabs.forEach(chatTab -> chatTab.getMessages().addFirst(message));
        }

        // message isn't loaded from chat history
        if (live) {
            // increment unread count for all matching chat tabs except the currently focused one, default tab included
            if (matchingChatTabs.isEmpty()) {
                if (DEFAULT_CHAT_TAB != FOCUSED_CHAT_TAB) {
                    DEFAULT_CHAT_TAB.setUnreadCount(DEFAULT_CHAT_TAB.getUnreadCount() + 1);

                    if (messageMeta.bestMatchingFilteredMessage() != null) {
                        DEFAULT_CHAT_TAB.setFilterTriggered(true);
                    }
                }
            } else {
                for (CustomChatTab chatTab : matchingChatTabs) {
                    if (chatTab == FOCUSED_CHAT_TAB) {
                        continue;
                    }

                    chatTab.setUnreadCount(chatTab.getUnreadCount() + 1);

                    if (messageMeta.bestMatchingFilteredMessage() != null) {
                        chatTab.setFilterTriggered(true);
                    }
                }
            }
        }

        // remove messages above limit for lag prevention
        int max = configuration.chat().getEffectiveMaxChatMessages();
        while (MESSAGE_CACHE.size() > max) {
            GuiMessage oldest = MESSAGE_CACHE.entrySet().stream()
                    .min(BY_TIMESTAMP)
                    .map(Map.Entry::getKey)
                    .orElseThrow();
            unregisterMessage(oldest);
        }
    }

    public static void applyFocusedChatTabMessages() {
        List<GuiMessage> targetMessages = FOCUSED_CHAT_TAB.getMessages();
        List<GuiMessage> allMessages = getAllMessages();
        allMessages.clear();
        allMessages.addAll(targetMessages);
        refreshTrimmedMessages();
    }

    public static boolean isValidPattern(String pattern) {
        return compiledPattern(pattern).isPresent();
    }

    public static Optional<Pattern> compiledPattern(String patternString) {
        if (patternString == null) {
            return Optional.empty();
        }

        return COMPILED_PATTERN_CACHE.computeIfAbsent(patternString, s -> {
            try {
                return Optional.of(compile(s, CASE_INSENSITIVE));
            } catch (PatternSyntaxException e) {
                return Optional.empty();
            }
        });
    }

    public static List<GuiMessage> getAllMessages() {
        ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
        return ((ChatComponentAccessor) chat).getAllMessages();
    }

    public static List<GuiMessage.Line> getTrimmedMessages() {
        ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
        return ((ChatComponentAccessor) chat).getTrimmedMessages();
    }

    public static int getChatScrollbarPos() {
        ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
        return ((ChatComponentAccessor) chat).getChatScrollbarPos();
    }

    public static void refreshTrimmedMessages() {
        ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
        ((ChatComponentAccessor) chat).invokeRefreshTrimmedMessages();
    }

    public static @Nullable GuiMessage getHoveredGuiMessage(double mouseX, double mouseY) {
        return ofNullable(getHoveredGuiMessageLine(mouseX, mouseY))
                .map(GuiMessage.Line::parent)
                .orElse(null);
    }

    public static GuiMessage.@Nullable Line getHoveredGuiMessageLine(double mouseX, double mouseY) {
        int entryHeight = 9;
        Options options = Minecraft.getInstance().options;
        int chatWidth = getWidth(options.chatWidth().get());
        int chatHeight = getHeight(options.chatHeightFocused().get());

        // verify mouseX
        if (mouseX < getChatLeft() || mouseX > chatWidth) {
            return null;
        }

        // verify mouseY
        if (mouseY < (getChatBottomHeight() - chatHeight) || mouseY > getChatBottomHeight()) {
            return null;
        }

        int currentLineIndex = getChatScrollbarPos();
        while (currentLineIndex < getTrimmedMessages().size()) {
            int lineYStart = getChatBottomHeight() - (currentLineIndex - getChatScrollbarPos() + 1) * entryHeight;
            int lineYEnd = getChatBottomHeight() - (currentLineIndex - getChatScrollbarPos()) * entryHeight;

            if (mouseY >= lineYStart && mouseY <= lineYEnd) {
                return getTrimmedMessages().get(currentLineIndex);
            }

            currentLineIndex++;
        }

        return null;
    }

    /**
     * The y coordinate of the divider line separating the messages that were unread when the currently focused chat tab got focused
     * from the older, already-read backlog above them, or {@code null} if there's nothing to divide. (No unread messages, or the
     * unread messages cover the entire visible backlog.)
     */
    public static @Nullable Integer getUnreadDividerY() {
        AbstractChatTab focusedChatTab = FOCUSED_CHAT_TAB;
        if (focusedChatTab.getUnreadCount() <= 0) {
            return null;
        }

        GuiMessage lastSeenParent = null;
        int distinctMessageCount = 0;

        for (GuiMessage.Line line : getTrimmedMessages()) {
            GuiMessage parent = line.parent();
            if (parent == lastSeenParent) {
                continue;
            }

            lastSeenParent = parent;
            distinctMessageCount++;

            // the first already-read message (right after the unread ones) marks where the divider belongs
            if (distinctMessageCount == focusedChatTab.getUnreadCount() + 1) {
                return getGuiMessageBounds(parent, 9).bottom();
            }
        }

        return null;
    }

    public static @NonNull ScreenRectangle getGuiMessageBounds(GuiMessage guiMessage, int entryHeight) {
        // get all lines for this GuiMessage
        List<GuiMessage.Line> lines = getTrimmedMessages().stream()
                .filter(l -> l.parent() == guiMessage)
                .toList();

        // get boundary lines and indexes
        GuiMessage.Line bottomLine = lines.getFirst();
        int bottomLineIndex = getTrimmedMessages().indexOf(bottomLine);
        GuiMessage.Line topLine = lines.getLast();
        int topLineIndex = getTrimmedMessages().indexOf(topLine);

        int scrollOffset = getChatScrollbarPos();

        // get boundary values
        int bottom = getChatBottomHeight() - bottomLineIndex * entryHeight + scrollOffset * entryHeight;
        int top = getChatBottomHeight() - (topLineIndex + 1) * entryHeight + scrollOffset * entryHeight;

        return new ScreenRectangle(getChatLeft(), top, getChatRight() - getChatLeft(), bottom - top);
    }

    public record MessageMeta(long receivedAt, @NonNull Set<CustomChatTab> matchingChatTabs, @Nullable FilteredMessage bestMatchingFilteredMessage) {

        public @NonNull ChatLogEntry toChatLogEntry(@NonNull GuiMessage message) {
            return new ChatLogEntry(message.content(), message.source(), this.receivedAt);
        }
    }
}
