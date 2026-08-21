package de.rettichlp.therettingtoncompanion.utils;

import com.mojang.blaze3d.platform.Window;
import de.rettichlp.therettingtoncompanion.configuration.ChatTab;
import de.rettichlp.therettingtoncompanion.gui.ChatTabButton;
import de.rettichlp.therettingtoncompanion.mixin.ChatComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static net.minecraft.client.gui.components.ChatComponent.getHeight;
import static net.minecraft.client.gui.components.ChatComponent.getWidth;
import static net.minecraft.util.Mth.ceil;
import static net.minecraft.util.Mth.floor;
import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;

public class ChatUtils {

    public static final List<ChatTabButton> CHAT_TAB_BUTTONS = new ArrayList<>();

    public static ChatTab FOCUSED_CHAT_TAB;

    // regex compilation is comparatively expensive and these patterns (chat tabs, filtered/hidden messages) are matched against every
    // chat message on every render/refresh, so recompiling the same pattern string on every single match call causes noticeable lag
    private static final Map<String, Optional<Pattern>> COMPILED_PATTERN_CACHE = new HashMap<>();

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
        String message = guiMessage.content().getString();
        ChatTab focusedChatTab = FOCUSED_CHAT_TAB;

        if (focusedChatTab != null) {
            return focusedChatTab.matches(message);
        }

        return configuration.chat().getChatTabs().stream().noneMatch(chatTab -> chatTab.matches(message));
    }

    public static boolean isValidPattern(String pattern) {
        return compiledPattern(pattern).isPresent();
    }

    /**
     * Compiles (case-insensitively) and caches the given pattern string, so repeated lookups of the same string (e.g. matching every
     * chat message against every chat tab's patterns) don't pay for regex compilation more than once. Returns an empty Optional for a
     * {@code null} or syntactically invalid pattern.
     */
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
     * from the older, already-read backlog above them, or {@code null} if there's nothing to divide. (No tab focused, no unread
     * messages, or the unread messages cover the entire visible backlog.)
     */
    public static @Nullable Integer getUnreadDividerY() {
        ChatTab focusedChatTab = FOCUSED_CHAT_TAB;
        if (focusedChatTab == null || focusedChatTab.getUnreadCount() <= 0) {
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
}
