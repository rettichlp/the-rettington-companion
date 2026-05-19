package de.rettichlp.therettingtoncompanion.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

@Data
@AllArgsConstructor
public class ChatRegex {

    private String pattern;
    private Identifier soundIdentifier;
    private boolean active;
    private int colorValue;
    private int priority;

    public ChatRegex(String pattern, Identifier soundIdentifier, boolean active, @NonNull Color color, int priority) {
        this.pattern = pattern;
        this.soundIdentifier = soundIdentifier;
        this.active = active;
        this.colorValue = color.getRGB();
        this.priority = priority;
    }

    public boolean isValidPattern() {
        try {
            compile(this.pattern);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    public Optional<Pattern> getCompiledPattern() {
        return isValidPattern() ? Optional.of(compile(this.pattern, CASE_INSENSITIVE)) : Optional.empty();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ChatRegex chatRegex
                && Objects.equals(this.pattern, chatRegex.getPattern())
                && this.active == chatRegex.isActive()
                && this.colorValue == chatRegex.getColorValue()
                && this.priority == chatRegex.getPriority();
    }

    public Color getColor() {
        return new Color(this.colorValue);
    }

    public void setColor(@NonNull Color color) {
        this.colorValue = color.getRGB();
    }
}
