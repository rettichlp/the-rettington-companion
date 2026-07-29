package de.rettichlp.therettingtoncompanion.utils;

import java.util.regex.PatternSyntaxException;

import static java.util.regex.Pattern.compile;

public class ChatUtils {

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
}
