package de.rettichlp.therettingtoncompanion.configuration;

import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.widgetService;
import static de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget.toNearestGridAnchorX;
import static de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget.toNearestGridAnchorY;

@Data
public class WidgetsConfiguration {

    private int size = 4;
    private int padding = 2;
    private Map<String, Object> widgets = new HashMap<>();

    public void setSize(int size) {
        this.size = size;

        // update widget positions
        widgetService.getInitializedWidgets().keySet().forEach(abstractTRCWidget -> {
            WidgetConfiguration widgetConfiguration = abstractTRCWidget.getWidgetConfiguration();
            widgetConfiguration.setX(toNearestGridAnchorX(widgetConfiguration.getX()));
            widgetConfiguration.setY(toNearestGridAnchorY(widgetConfiguration.getY()));
        });
    }
}
