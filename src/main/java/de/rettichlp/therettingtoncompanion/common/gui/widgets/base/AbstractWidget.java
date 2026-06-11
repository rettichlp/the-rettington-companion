package de.rettichlp.therettingtoncompanion.common.gui.widgets.base;

import com.google.common.reflect.TypeToken;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.notificationService;
import static de.rettichlp.therettingtoncompanion.common.utils.ModUtils.GSON;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static net.minecraft.network.chat.Component.translatable;

@Getter
public abstract class AbstractWidget<C extends WidgetConfiguration> {

    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    private final Font font = Minecraft.getInstance().font;

    private C widgetConfiguration;

    public abstract Component getDisplayName();

    public abstract Component getTooltip();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y, Alignment alignment);

    public void init() {
        loadConfiguration();
    }

    public void draw(@NotNull GuiGraphicsExtractor graphics) {
        if (!isVisible() || !this.widgetConfiguration.isEnabled() || Minecraft.getInstance().options.hideGui) {
            return;
        }

        int x = (int) this.widgetConfiguration.getX();
        int y = (int) this.widgetConfiguration.getY();
        draw(graphics, x, y, getAlignment());
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        double x = this.widgetConfiguration.getX();
        double y = this.widgetConfiguration.getY();
        boolean mouseOverX = mouseX >= x && mouseX <= x + getWidth();
        boolean mouseOverY = mouseY >= y && mouseY <= y + getHeight();
        return mouseOverX && mouseOverY;
    }

    public boolean isVisible() {
        return true;
    }

    public String getRegistryName() {
        return ofNullable(this.getClass().getAnnotation(Widget.class))
                .map(Widget::registryName)
                .orElseThrow(() -> new IllegalStateException("Widget class " + this.getClass().getName() + " has no registry name"));
    }

    public void loadConfiguration() {
        String registryName = getRegistryName();

        if (isNull(registryName)) {
            LOGGER.warn("Widget {} is missing registry name and therefore has no configuration", this.getClass().getName());
            return;
        }

        Class<C> widgetConfigurationClass = getConfigurationClass();
        // load configuration from the configuration file - not from the cache
        Object widgetConfigurationObject = configuration.loadFromFile().getWidgets().get(registryName);

        if (isNull(widgetConfigurationObject)) {
            LOGGER.info("No configuration found for widget {}, using default configuration", registryName);

            try {
                this.widgetConfiguration = widgetConfigurationClass.getConstructor().newInstance();
                this.widgetConfiguration.setEnabled(getDefaultEnabled());
                this.widgetConfiguration.setX(getDefaultX());
                this.widgetConfiguration.setY(getDefaultY());
            } catch (Exception e) {
                notificationService.sendErrorNotification(translatable("trc.notification.configuration_not_loaded", translatable(registryName)));
                LOGGER.error("Could not load configuration for widget {}", registryName, e);
            }

            return;
        }

        String widgetConfigurationJson = GSON.toJson(widgetConfigurationObject);

        this.widgetConfiguration = GSON.fromJson(widgetConfigurationJson, widgetConfigurationClass);
    }

    public void saveConfiguration() {
        String registryName = getRegistryName();

        if (isNull(registryName)) {
            LOGGER.warn("Widget {} is missing registry name and therefore has no configuration", this.getClass().getName());
            return;
        }

        C widgetConfiguration = getWidgetConfiguration();
        widgetConfiguration.setX(roundToNearestHalf(widgetConfiguration.getX()));
        widgetConfiguration.setY(roundToNearestHalf(widgetConfiguration.getY()));

        String widgetConfigurationJson = GSON.toJson(widgetConfiguration);
        Map<String, Object> widgetConfigurationMap = GSON.fromJson(widgetConfigurationJson, MAP_TYPE);
        configuration.getWidgets().put(registryName, widgetConfigurationMap);
    }

    private Alignment getAlignment() {
        int scaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int widthSegment = scaledWidth / 3;

        Alignment alignment;

        double x = this.widgetConfiguration.getX();
        if (x <= widthSegment) {
            alignment = Alignment.LEFT;
        } else if (x <= widthSegment * 2) {
            alignment = Alignment.CENTER;
        } else {
            alignment = Alignment.RIGHT;
        }

        return alignment;
    }

    private boolean getDefaultEnabled() {
        return ofNullable(this.getClass().getAnnotation(Widget.class))
                .map(Widget::defaultEnabled)
                .orElse(false);
    }

    private double getDefaultX() {
        return ofNullable(this.getClass().getAnnotation(Widget.class))
                .map(Widget::defaultX)
                .orElse(0.0);
    }

    private double getDefaultY() {
        return ofNullable(this.getClass().getAnnotation(Widget.class))
                .map(Widget::defaultY)
                .orElse(0.0);
    }

    @SuppressWarnings("unchecked")
    private Class<C> getConfigurationClass() {
        Type type = getClass().getGenericSuperclass();

        if (type instanceof ParameterizedType) {
            Type[] typeArgs = ((ParameterizedType) type).getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                return (Class<C>) typeArgs[0];
            }
        }

        throw new IllegalStateException("Widget class must be generic: AbstractUCUtilsWidget<C>");
    }

    private double roundToNearestHalf(double value) {
        return Math.round(value * 2) / 2.0;
    }

    public enum Alignment {

        LEFT,
        CENTER,
        RIGHT
    }
}
