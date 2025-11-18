package de.rettichlp.therettingtoncompanion.common.utils;

import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;

import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Matcher;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;

public class TextUtils {

    public static void checkForHighlightedMessageAndRun(String message, Runnable runnable) {
        if (configuration.isDefaultChatRegex() && message.equalsIgnoreCase(player.getGameProfile().name())) {
            runnable.run();
        }

        configuration.getChatRegexes().stream()
                .filter(ChatRegex::isActive)
                .sorted(Comparator.comparingInt(ChatRegex::getPriority))
                .map(ChatRegex::getCompiledPattern)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(pattern -> pattern.matcher(message))
                .filter(Matcher::find)
                .forEach(matcher -> runnable.run());
    }
}
