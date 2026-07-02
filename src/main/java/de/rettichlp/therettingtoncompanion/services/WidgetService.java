package de.rettichlp.therettingtoncompanion.services;

import de.rettichlp.therettingtoncompanion.TheRettingtonCompanionApi;
import de.rettichlp.therettingtoncompanion.gui.widgets.CountdownWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.DateTimeWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.HearthWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static java.util.Set.copyOf;
import static net.minecraft.network.chat.Component.literal;

public class WidgetService {

    private final Set<AbstractTRCWidget<?>> widgets = Set.of(
            new DateTimeWidget(),
            new HearthWidget(),
            new CountdownWidget(literal("test"), LocalDateTime.now(), 30000)
    );

    public void initWidgets() {
        getWidgets().forEach(AbstractTRCWidget::init);
    }

    public Set<AbstractTRCWidget<?>> getWidgets() {
        Collection<AbstractTRCWidget<?>> widgets = new ArrayList<>(this.widgets);

        // load widgets from other mods
        FabricLoader.getInstance().getEntrypointContainers(MOD_ID, TheRettingtonCompanionApi.class).forEach(container -> {
            TheRettingtonCompanionApi entrypoint = container.getEntrypoint();
            Set<AbstractTRCWidget<?>> otherModWidgets = entrypoint.getWidgets();
            widgets.addAll(otherModWidgets);
        });

        return copyOf(widgets);
    }

    public Color getSecondaryColor(@NonNull Color color) {
        return new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2, 100);
    }
}
