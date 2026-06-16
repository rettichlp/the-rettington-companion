package de.rettichlp.therettingtoncompanion.models;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static java.awt.Color.GREEN;
import static java.util.Comparator.comparingInt;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT;
import static net.minecraft.resources.Identifier.parse;
import static net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL;

@Setter
public class ChatRegex {

    private static final SoundEvent DEFAULT_SOUND = NOTE_BLOCK_BELL.value();

    private String patternString;
    private String soundIdentifierString;
    @Getter
    private boolean active;
    private int colorValue;
    @Getter
    private int priority;

    public ChatRegex(String patternString) {
        this.patternString = patternString;
        this.soundIdentifierString = DEFAULT_SOUND.location().toString();
        this.active = true;
        this.colorValue = GREEN.getRGB();
        this.priority = 5;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ChatRegex chatRegex
                && this.patternString.equalsIgnoreCase(chatRegex.patternString)
                && this.soundIdentifierString.equalsIgnoreCase(chatRegex.soundIdentifierString)
                && this.active == chatRegex.isActive()
                && this.colorValue == chatRegex.colorValue
                && this.priority == chatRegex.getPriority();
    }

    public Optional<Pattern> getPattern() {
        return isValidPattern(this.patternString) ? Optional.of(compile(this.patternString, CASE_INSENSITIVE)) : Optional.empty();
    }

    public Identifier getSoundIdentifier() {
        return isValidSoundIdentifier(this.soundIdentifierString) ? parse(this.soundIdentifierString) : DEFAULT_SOUND.location();
    }

    public Color getColor() {
        return new Color(this.colorValue);
    }

    public void setColor(@NonNull Color color) {
        this.colorValue = color.getRGB();
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

    public static boolean isValidSoundIdentifier(String identifierString) {
        if (identifierString == null) {
            return false;
        }

        Identifier parsed = parse(identifierString);
        return SOUND_EVENT.containsKey(parsed);
    }

    public static @Nullable ChatRegex getBestMatchingChatRegex(String message) {
        // matches default regex
        ChatRegex defaulChatRegex = configuration.chat().regex().getDefaulChatRegex();
        if (matchesDefaultRegex(defaulChatRegex, message)) {
            return defaulChatRegex;
        }

        // matches custom regexes
        List<ChatRegex> matchingCustomRegexes = getMatchingCustomRegexes(message);
        return matchingCustomRegexes.isEmpty() ? null : matchingCustomRegexes.getFirst();
    }

    private static boolean matchesDefaultRegex(@NonNull ChatRegex defaulChatRegex, String message) {
        return defaulChatRegex.isActive() && message.toLowerCase().contains(player.getGameProfile().name().toLowerCase());
    }

    private static @NonNull @Unmodifiable List<ChatRegex> getMatchingCustomRegexes(CharSequence message) {
        return configuration.chat().regex().getChatRegexes().stream()
                .filter(ChatRegex::isActive)
                .filter(chatRegex -> chatRegex.getPattern().map(pattern -> pattern.matcher(message).find()).orElse(false))
                .sorted(comparingInt(ChatRegex::getPriority).reversed())
                .toList();
    }
}
