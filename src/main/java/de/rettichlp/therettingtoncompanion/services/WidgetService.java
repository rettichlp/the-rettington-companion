package de.rettichlp.therettingtoncompanion.services;

import de.rettichlp.therettingtoncompanion.TheRettingtonCompanionApi;
import de.rettichlp.therettingtoncompanion.gui.widgets.CountdownWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.DateTimeWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.HearthWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.NotificationGroupWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static java.lang.Math.clamp;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;
import static net.minecraft.network.chat.Component.literal;

public class WidgetService {

    private final Set<AbstractTRCWidget<?>> widgets = Set.of(
            new CountdownWidget(literal("test"), now(), 30000),
            new DateTimeWidget(),
            new HearthWidget(),
            new NotificationGroupWidget()
    );

    public void initWidgets() {
        getWidgets().forEach(AbstractTRCWidget::init);
    }

    public List<AbstractTRCWidget<?>> getWidgets() {
        List<AbstractTRCWidget<?>> widgets = new ArrayList<>();

        this.widgets.stream()
                .filter(abstractTRCWidget -> abstractTRCWidget.getRegistryName() != null)
                .sorted(comparing(AbstractTRCWidget::getRegistryName))
                .forEach(widgets::add);

        // load widgets from other mods
        FabricLoader.getInstance().getEntrypointContainers(MOD_ID, TheRettingtonCompanionApi.class).forEach(container -> {
            TheRettingtonCompanionApi entrypoint = container.getEntrypoint();
            widgets.addAll(entrypoint.getWidgets());
        });

        return widgets;
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
