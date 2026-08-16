package de.rettichlp.therettingtoncompanion.utils;

import com.mojang.blaze3d.platform.Window;
import de.rettichlp.therettingtoncompanion.configuration.ChatTab;
import de.rettichlp.therettingtoncompanion.gui.ChatTabButton;
import de.rettichlp.therettingtoncompanion.mixin.ChatComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static net.minecraft.client.gui.components.ChatComponent.getHeight;
import static net.minecraft.client.gui.components.ChatComponent.getWidth;
import static net.minecraft.util.Mth.ceil;
import static net.minecraft.util.Mth.floor;
import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;

public class ChatUtils {

    public static final List<ChatTabButton> CHAT_TAB_BUTTONS = new ArrayList<>();

    public static ChatTab FOCUSED_CHAT_TAB;

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
        return getChatLeft() + getWidth(options.chatWidth().get());
    }

    public static boolean isMessageVisible(@NonNull GuiMessage guiMessage) {
        String message = guiMessage.content().getString();
        ChatTab focusedChatTab = FOCUSED_CHAT_TAB;

        if (focusedChatTab != null) {
            return focusedChatTab.matches(message);
        }

        return configuration.chat().getChatTabs().stream().noneMatch(chatTab -> chatTab.matches(message));
    }

    public static void layoutChatTabButtons(@NonNull List<? extends AbstractWidget> chatTabButtonsInDisplayOrder) {
        int spacing = 2;
        int rowHeight = 14;
        int leftEdge = getChatLeft();
        int rightEdge = getChatRight() + 12 - 2;

        int currentX = leftEdge;
        int currentRowY = getChatTopHeight() - spacing - rowHeight;

        // lay out left-to-right; once a row runs out of horizontal space, wrap to a new row directly above it
        for (AbstractWidget chatTabButton : chatTabButtonsInDisplayOrder) {
            int width = chatTabButton.getWidth();

            if (currentX + width > rightEdge && currentX != leftEdge) {
                currentRowY -= (rowHeight + spacing);
                currentX = leftEdge;
            }

            chatTabButton.setPosition(currentX, currentRowY);
            currentX += (width + spacing);
        }
    }

    public static boolean isValidPattern(String pattern) {
        if (pattern == null) {
            return false;
        }

        try {
            compile(pattern);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
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
        int left = 0;
        int width = getWidth(Minecraft.getInstance().options.chatWidth().get()) + 12; // I don't know from where the offset of 12 comes

        return new ScreenRectangle(left + getChatLeft(), top, width - getChatLeft(), bottom - top);
    }
}
