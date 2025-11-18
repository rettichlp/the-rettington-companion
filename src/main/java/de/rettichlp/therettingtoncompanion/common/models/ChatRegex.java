package de.rettichlp.therettingtoncompanion.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.regex.Pattern.compile;

@Data
@AllArgsConstructor
public class ChatRegex {

    private String pattern;
    private boolean active;
    private int priority;

    public Optional<Pattern> getCompiledPattern() {
        try {
            return Optional.of(compile(this.pattern));
        } catch (PatternSyntaxException e) {
            return Optional.empty();
        }
    }
}
