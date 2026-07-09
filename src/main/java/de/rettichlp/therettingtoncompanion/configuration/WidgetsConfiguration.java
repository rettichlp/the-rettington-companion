package de.rettichlp.therettingtoncompanion.configuration;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WidgetsConfiguration {

    private int size = 4;
    private Map<String, Object> widgets = new HashMap<>();
}
