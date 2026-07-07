package de.rettichlp.therettingtoncompanion.configuration;

import lombok.Data;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

@Data
public class WidgetsConfiguration {

    private int size = 8;
    private Map<String, Object> widgets = new HashMap<>();

    public int getWidgetPositionScale(@NonNull Minecraft minecraft) {
        return this.size / minecraft.options.guiScale().get();
    }
}
