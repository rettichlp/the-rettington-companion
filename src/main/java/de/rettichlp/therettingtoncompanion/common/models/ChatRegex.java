package de.rettichlp.therettingtoncompanion.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Pattern;

@Data
@AllArgsConstructor
public class ChatRegex {

    private Pattern pattern;
    private boolean active;
    private int priority;
}
