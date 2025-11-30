package de.rettichlp.therettingtoncompanion.listeners;

import de.rettichlp.therettingtoncompanion.common.registry.IMessageReceiveListener;
import de.rettichlp.therettingtoncompanion.common.registry.Listener;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.getHighlightColor;
import static net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_BELL;

@Listener
public class ChatListener implements IMessageReceiveListener {

    @Override
    public boolean onMessageReceive(Text text, @NotNull String message) {
        Formatting highlightColor = getHighlightColor(message);
        if (highlightColor != null) {
            player.playSound(BLOCK_NOTE_BLOCK_BELL.value(), 1, 1.25f);
        }
        return true;
    }
}
