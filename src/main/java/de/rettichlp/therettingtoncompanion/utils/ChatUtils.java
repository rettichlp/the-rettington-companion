package de.rettichlp.therettingtoncompanion.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.NonNull;

import java.util.regex.PatternSyntaxException;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.regex.Pattern.compile;
import static net.minecraft.util.Mth.ceil;
import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;

public class ChatUtils {

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
}
