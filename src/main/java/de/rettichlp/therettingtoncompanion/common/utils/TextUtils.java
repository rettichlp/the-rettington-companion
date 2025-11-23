package de.rettichlp.therettingtoncompanion.common.utils;

import com.mojang.brigadier.Message;
import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static java.lang.Character.toChars;
import static java.util.Comparator.comparingInt;

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
     * Determines the highlight color for a given message based on default or custom chat regex rules. If the message matches the
     * default regex configuration, the corresponding default highlight color is returned. Otherwise, if the message matches custom
     * regex rules, the highlight color of the highest-priority matching rule is returned.
     *
     * @param message the message to evaluate for highlight color determination; must not be null.
     *
     * @return the {@link Formatting} representing the highlight color if a matching rule is found; {@code null} if no match is found.
     */
    public static @Nullable Formatting getHighlightColor(String message) {
        if (matchesDefaultRegex(message)) {
            return configuration.chat().regex().getDefaultChatRegexStyle();
        }

        List<ChatRegex> matchingCustomRegexes = getMatchingCustomRegexes(message);
        return matchingCustomRegexes.isEmpty() ? null : matchingCustomRegexes.getFirst().getColor();
    }

    /**
     * Determines if the given message matches the default chat regex configuration and contains the player's game profile name
     * (case-insensitive).
     *
     * @param message the message to be checked against the default regex and the player's game profile name; must not be null.
     *
     * @return {@code true} if the message matches the default chat regex and contains the player's game profile name; {@code false}
     *         otherwise.
     */
    private static boolean matchesDefaultRegex(String message) {
        return configuration.chat().regex().isDefaultChatRegex()
                && message.toLowerCase().contains(player.getGameProfile().name().toLowerCase());
    }

    /**
     * Retrieves a list of custom chat regex rules that match the given message. The regex rules are filtered based on their active
     * status and whether their compiled pattern finds a match in the provided message. The resulting list is sorted by the priority of
     * the regex rules in ascending order.
     *
     * @param message the input message to check against the custom regex rules; must not be null.
     *
     * @return an unmodifiable list of {@link ChatRegex} objects that match the given message.
     */
    private static @NotNull @Unmodifiable List<ChatRegex> getMatchingCustomRegexes(CharSequence message) {
        return configuration.chat().regex().getChatRegexes().stream()
                .sorted(comparingInt(ChatRegex::getPriority))
                .filter(ChatRegex::isActive)
                .filter(chatRegex -> {
                    Optional<Pattern> compiledPattern = chatRegex.getCompiledPattern();
                    return compiledPattern.isPresent() && compiledPattern.get().matcher(message).find();
                })
                .toList();
    }
}
