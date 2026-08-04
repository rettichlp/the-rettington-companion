package de.rettichlp.therettingtoncompanion.services;

import de.rettichlp.therettingtoncompanion.TheRettingtonCompanionApi;
import de.rettichlp.therettingtoncompanion.gui.widgets.DateTimeWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.HearthWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.NotificationGroupWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.time.temporal.Temporal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static java.lang.Math.clamp;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;

public class WidgetService {

    private final Set<AbstractTRCWidget<?>> widgets = Set.of(
            new DateTimeWidget(),
            new HearthWidget(),
            new NotificationGroupWidget()
    );

    @Getter
    private final Map<AbstractTRCWidget<?>, String> initializedWidgets = new LinkedHashMap<>();

    public void initWidgets() {
        LinkedHashMap<AbstractTRCWidget<?>, String> widgetsToInitialize = new LinkedHashMap<>();

        // load widgets from this mod
        this.widgets.stream()
                .filter(abstractTRCWidget -> abstractTRCWidget.getRegistryName() != null)
                .sorted(comparing(AbstractTRCWidget::getRegistryName))
                .forEach(abstractTRCWidget -> widgetsToInitialize.put(abstractTRCWidget, MOD_ID));

        // load widgets from other mods
        FabricLoader.getInstance().getEntrypointContainers(MOD_ID, TheRettingtonCompanionApi.class).forEach(container -> {
            String providerId = container.getProvider().getMetadata().getId();
            container.getEntrypoint().getWidgets().forEach(widget -> widgetsToInitialize.put(widget, providerId));
        });

        widgetsToInitialize.forEach((abstractTRCWidget, providingModId) -> {
            abstractTRCWidget.init();
            this.initializedWidgets.put(abstractTRCWidget, providingModId);
        });

        LOGGER.info("Initialized {} widgets", this.initializedWidgets.size());
    }

    public Color getTransparentColor(@NonNull Color color) {
        return new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2, 100);
    }

    public static double calculateProgress(Temporal creationTime, long durationInMillis) {
        long elapsedMillis = between(creationTime, now()).toMillis();
        double progress = (double) elapsedMillis / durationInMillis;
        return clamp(progress, 0.0, 1.0);
    }
}
