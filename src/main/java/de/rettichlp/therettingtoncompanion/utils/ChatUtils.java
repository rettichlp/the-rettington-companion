package de.rettichlp.therettingtoncompanion.utils;

import de.rettichlp.therettingtoncompanion.mixin.ChatComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.regex.PatternSyntaxException;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.regex.Pattern.compile;
import static net.minecraft.client.gui.components.ChatComponent.getHeight;
import static net.minecraft.client.gui.components.ChatComponent.getWidth;
import static net.minecraft.util.Mth.ceil;
import static net.minecraft.util.Mth.floor;
import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;

public class ChatUtils {

    public static GuiMessage LAST_HOVERED_GUI_MESSAGE;

    public static int getChatWidth() {
        Minecraft minecraft = Minecraft.getInstance();
        double originMinecraftChatWidth = getWidth(minecraft.options.chatWidth().get());

        if (!configuration.chat().isOptimizedChat()) {
            return (int) originMinecraftChatWidth;
        }

        double trcMinecraftChatWidth = minecraft.getWindow().getGuiScaledWidth() / 2.0 - 12; // I don't know why, but 12px offset
        return (int) max(originMinecraftChatWidth, trcMinecraftChatWidth);
    }

    public static int getChatHeight() {
        Minecraft minecraft = Minecraft.getInstance();
        double originMinecraftChatHeight = getHeight(minecraft.options.chatHeightFocused().get());

        if (!configuration.chat().isOptimizedChat()) {
            return (int) originMinecraftChatHeight;
        }

        double trcMinecraftChatHeight = minecraft.getWindow().getGuiScaledHeight() / 2.0;
        return (int) (minecraft.gui.hud.getChat().isChatFocused() ? (max(trcMinecraftChatHeight, originMinecraftChatHeight)) : originMinecraftChatHeight);
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

    public static @NonNull ScreenRectangle getGuiMessageBounds(GuiMessage.Line line, int entryHeight) {
        // get all lines for this GuiMessage
        List<GuiMessage.Line> lines = getTrimmedMessages().stream()
                .filter(l -> l.parent().equals(line.parent()))
                .toList();

        // get boundary lines and indexes
        GuiMessage.Line bottomLine = lines.getFirst();
        int bottomLineIndex = getTrimmedMessages().indexOf(bottomLine);
        GuiMessage.Line topLine = lines.getLast();
        int topLineIndex = getTrimmedMessages().indexOf(topLine);

        // get boundary values
        int bottom = getChatBottomHeight() - bottomLineIndex * entryHeight;
        int top = getChatBottomHeight() - (topLineIndex + 1) * entryHeight;
        int left = -4;
        int right = getChatWidth() + 4 + 4;

        return new ScreenRectangle(left, top, right - left, bottom - top);
    }
}
