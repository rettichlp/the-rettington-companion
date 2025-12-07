package de.rettichlp.therettingtoncompanion.listeners;

import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import de.rettichlp.therettingtoncompanion.common.registry.IMessageReceiveListener;
import de.rettichlp.therettingtoncompanion.common.registry.Listener;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.getHighestPriorityMatchingChatRegex;
import static net.minecraft.registry.Registries.SOUND_EVENT;

@Listener
public class ChatListener implements IMessageReceiveListener {

    @Override
    public boolean onMessageReceive(Text text, @NotNull String message) {
        ChatRegex chatRegex = getHighestPriorityMatchingChatRegex(message);
        if (chatRegex != null) {
            SoundEvent soundEvent = SOUND_EVENT.get(chatRegex.getSoundIdentifier());
            player.playSound(soundEvent, 1, 1.25f);
        }
        return true;
    }
}
