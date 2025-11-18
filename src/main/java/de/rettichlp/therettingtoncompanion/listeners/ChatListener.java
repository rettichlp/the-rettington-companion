package de.rettichlp.therettingtoncompanion.listeners;

import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import de.rettichlp.therettingtoncompanion.common.registry.IMessageReceiveListener;
import de.rettichlp.therettingtoncompanion.common.registry.Listener;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Matcher;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_BELL;

@Listener
public class ChatListener implements IMessageReceiveListener {

    @Override
    public boolean onMessageReceive(Text text, @NotNull String message) {
        configuration.getChatRegexes().stream()
                .filter(ChatRegex::isActive)
                .sorted(Comparator.comparingInt(ChatRegex::getPriority))
                .map(ChatRegex::getCompiledPattern)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(pattern -> pattern.matcher(message))
                .filter(Matcher::find)
                .forEach(matcher -> player.playSound(BLOCK_NOTE_BLOCK_BELL.value(), 1, 1.25f));

        return true;
    }
}
