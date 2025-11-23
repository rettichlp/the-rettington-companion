package de.rettichlp.therettingtoncompanion.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

@Data
@AllArgsConstructor
public class ChatRegex {

    private String pattern;
    private boolean active;
    private Formatting color;
    private int priority;

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
}
