package de.rettichlp.therettingtoncompanion.gui.widgets.base;

import lombok.Data;

import java.awt.Color;

import static java.awt.Color.WHITE;

@Data
public class WidgetConfiguration {

    private boolean enabled = false;
    private double x = 0.0;
    private double y = 0.0;
    private Color color = WHITE;
}
