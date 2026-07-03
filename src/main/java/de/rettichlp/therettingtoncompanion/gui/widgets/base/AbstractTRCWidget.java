package de.rettichlp.therettingtoncompanion.gui.widgets.base;

import com.google.common.reflect.TypeToken;
import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.notificationService;
import static de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget.Alignment.CENTER_LEFT;
import static de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget.Alignment.TOP_LEFT;
import static de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget.Alignment.TOP_RIGHT;
import static de.rettichlp.therettingtoncompanion.utils.ModUtils.GSON;
import static java.lang.Math.clamp;
import static java.util.Objects.isNull;
import static net.minecraft.network.chat.Component.translatable;

@Getter
public abstract class AbstractTRCWidget<C extends WidgetConfiguration> {

    public static final int WIDGET_POSITION_SCALE = 4;
    public static final int TEXT_BOX_PADDING = 3;

    protected final Minecraft minecraft = Minecraft.getInstance();

    @Setter
    protected boolean focused;

    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    private C widgetConfiguration;

    public abstract String getRegistryName();

    public abstract Component getLabel();

    public abstract Component getTooltip();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void extractWidget(@NotNull GuiGraphicsExtractor graphics, int x, int y, Alignment alignment);

    public abstract void addOptions(@NonNull TRCOptionsList optionsList);

    public void init() {
        loadConfiguration();
    }

    public void extractWidget(Minecraft minecraft, @NotNull GuiGraphicsExtractor graphics) {
        this.minecraft = minecraft;

        if (!isVisible() || !this.widgetConfiguration.isEnabled() || Minecraft.getInstance().options.hideGui) {
            return;
        }

        int x = (int) clamp(this.widgetConfiguration.getX(), 0, graphics.guiWidth() - getWidth());
        int y = (int) clamp(this.widgetConfiguration.getY(), 0, graphics.guiHeight() - getHeight());

        if (this.focused) {
            graphics.horizontalLine(0, graphics.guiWidth(), y, YELLOW.getRGB());
            graphics.verticalLine(x, 0, graphics.guiHeight(), YELLOW.getRGB());
        }

        if (isWidgetPositionScreen()) {
            graphics.fill(x, y, x + getWidth(), y + getHeight(), BLUE.getRGB());
            graphics.outline(x, y, getWidth(), getHeight(), CYAN.getRGB());
        }

        extractWidget(graphics,  x, y, getAlignment());
    }

    public double getRight() {
        return getWidgetConfiguration().getX() + getWidth();
    }

    public double getBottom() {
        return getWidgetConfiguration().getY() + getHeight();
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
            } catch (Exception e) {
                notificationService.sendErrorNotification(translatable("trc.notification.configuration_not_loaded"));
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
            alignment = TOP_LEFT;
        } else if (x <= widthSegment * 2) {
            alignment = CENTER_LEFT;
        } else {
            alignment = TOP_RIGHT;
        }

        return alignment;
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

        throw new IllegalStateException("Widget class must be generic: AbstractTRCWidget<C>");
    }

    public static long toNearestScale(double value) {
        return Math.round(value / WIDGET_POSITION_SCALE) * WIDGET_POSITION_SCALE;
    }

    public enum Alignment {

        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        CENTER_LEFT,
        CENTER_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT
    }
}
