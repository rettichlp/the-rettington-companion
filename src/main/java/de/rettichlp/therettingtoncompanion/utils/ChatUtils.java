package de.rettichlp.therettingtoncompanion.utils;

import de.rettichlp.therettingtoncompanion.mixin.ChatComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.regex.PatternSyntaxException;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.regex.Pattern.compile;
import static net.minecraft.util.Mth.ceil;
import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;

public class ChatUtils {

    public static GuiMessage LAST_HOVERED_GUI_MESSAGE;

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

    public static int getChatBottomHeight(@NonNull Minecraft minecraft, @NonNull LocalPlayer player) {
        int yLineBase = minecraft.getWindow().getGuiScaledHeight() - 39;
        float maxHealth = max((float) player.getAttributeValue(MAX_HEALTH), player.getHealth());
        int totalAbsorption = ceil(player.getAbsorptionAmount());
        int numHealthRows = ceil((maxHealth + (float) totalAbsorption) / 2.0F / 10.0F);
        int healthRowHeight = max(10 - (numHealthRows - 2), 3);
        int yLineArmor = yLineBase - (numHealthRows - 1) * healthRowHeight - 10;
        int yLineChatBottom = player.getArmorValue() > 0 ? yLineArmor : yLineArmor + 10;
        return min(minecraft.getWindow().getGuiScaledHeight() - 47, yLineChatBottom); // same height as empty inventory slot count
    }

    public static List<GuiMessage> getAllMessages() {
        ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
        return ((ChatComponentAccessor) chat).getAllMessages();
    }

    public static List<GuiMessage.Line> getTrimmedMessages() {
        ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
        return ((ChatComponentAccessor) chat).getTrimmedMessages();
    }

    public static @NonNull ScreenRectangle getGuiMessageBounds(GuiMessage.Line line, int chatBottom, int maxWidth, int entryHeight) {
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
        int bottom = chatBottom - bottomLineIndex * entryHeight;
        int top = chatBottom - (topLineIndex + 1) * entryHeight;
        int left = -4;
        int right = maxWidth + 4 + 4;

        return new ScreenRectangle(left, top, right - left, bottom - top);
    }
}
