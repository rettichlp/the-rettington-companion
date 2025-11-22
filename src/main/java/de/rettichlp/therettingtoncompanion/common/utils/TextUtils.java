package de.rettichlp.therettingtoncompanion.common.utils;

import com.mojang.brigadier.Message;
import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import net.minecraft.text.OrderedText;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Matcher;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static java.lang.Character.toChars;

public class TextUtils {

    /**
     * Converts the given object into its string representation.
     *
     * @param object the object to convert.
     *
     * @return the string representation of the provided object.
     *
     * @throws IllegalArgumentException if the object type is unsupported.
     */
    public static String getString(@NotNull Object object) {
        return switch (object) {
            case Message message -> message.getString();
            case OrderedText orderedText -> {
                StringBuilder builder = new StringBuilder();
                orderedText.accept((index, style, codePoint) -> {
                    builder.append(toChars(codePoint));
                    return true;
                });

                yield builder.toString();
            }
            default -> throw new IllegalArgumentException("Unsupported text object: " + object.getClass().getName());
        };
    }

    /**
     * Determines if a given message should be highlighted based on the current chat configuration.
     * <p>
     * This method checks if the message matches a series of active regular expressions in the chat configuration or if the message
     * contains the player's name and the default chat regex is enabled.
     *
     * @param message the message to be checked.
     *
     * @return {@code true} if the message should be highlighted; {@code false} otherwise.
     */
    public static boolean shouldBeHighlighted(String message) {
        if (configuration.isDefaultChatRegex() && message.toLowerCase().contains(player.getGameProfile().name().toLowerCase())) {
            return true;
        }

        return configuration.getChatRegexes().stream()
                .filter(ChatRegex::isActive)
                .sorted(Comparator.comparingInt(ChatRegex::getPriority))
                .map(ChatRegex::getCompiledPattern)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(pattern -> pattern.matcher(message))
                .anyMatch(Matcher::find);
    }

    /**
     * Checks if a given message should be highlighted and, if so, executes the specified {@link Runnable}. This method uses the
     * highlighting logic defined in {@link #shouldBeHighlighted(String)}.
     *
     * @param message  the message to check for highlighting; must not be null.
     * @param runnable the code to execute if the message is highlighted; must not be null.
     */
    public static void checkForHighlightedMessageAndRun(String message, Runnable runnable) {
        if (shouldBeHighlighted(message)) {
            runnable.run();
        }
    }
}
