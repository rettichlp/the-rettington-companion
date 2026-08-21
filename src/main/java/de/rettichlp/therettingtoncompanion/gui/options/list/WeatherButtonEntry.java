package de.rettichlp.therettingtoncompanion.gui.options.list;

import de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.WeatherValue;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.WeatherValue.W_CLEAR;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.WeatherValue.W_OFF;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.WeatherValue.W_RAIN;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.WeatherValue.W_THUNDER;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class WeatherButtonEntry extends AbstractEntry {

    private static final int BUTTON_SIZE_X = 32;
    private static final int BUTTON_SIZE_Y = 20;
    private static final int ICON_SIZE = 12;
    private static final int PADDING = 2;

    private final StringWidget stringWidget;
    private final Button offButton;
    private final Button sunButton;
    private final Button rainButton;
    private final Button thunderButton;

    protected WeatherButtonEntry(Font font,
                                 Component label,
                                 Tooltip tooltip,
                                 Consumer<WeatherValue> valueChangeListener) {
        this.stringWidget = new StringWidget(label, font);
        this.stringWidget.setTooltip(tooltip);
        this.offButton = button(W_OFF, valueChangeListener);
        this.sunButton = button(W_CLEAR, valueChangeListener);
        this.rainButton = button(W_RAIN, valueChangeListener);
        this.thunderButton = button(W_THUNDER, valueChangeListener);
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.stringWidget.setPosition(getContentX(), getContentYMiddle() - this.stringWidget.getHeight() / 2);
        this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);

        int y = getContentYMiddle() - BUTTON_SIZE_Y / 2;
        extractWeatherButton(graphics, this.offButton, W_OFF, "off", getContentRight() - 4 * BUTTON_SIZE_X - 3 * PADDING + 2, y, mouseX, mouseY, a);
        extractWeatherButton(graphics, this.sunButton, W_CLEAR, "sun", getContentRight() - 3 * BUTTON_SIZE_X - 2 * PADDING + 2, y, mouseX, mouseY, a);
        extractWeatherButton(graphics, this.rainButton, W_RAIN, "rain", getContentRight() - 2 * BUTTON_SIZE_X - PADDING + 2, y, mouseX, mouseY, a);
        extractWeatherButton(graphics, this.thunderButton, W_THUNDER, "thunder", getContentRight() - BUTTON_SIZE_X + 2, y, mouseX, mouseY, a);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.stringWidget, this.offButton, this.sunButton, this.rainButton, this.thunderButton);
    }

    private @NonNull Button button(@NonNull WeatherValue value, Consumer<WeatherValue> valueChangeListener) {
        return Button.builder(empty(), _ -> valueChangeListener.accept(value))
                .size(BUTTON_SIZE_X, 20)
                .tooltip(value.tooltip())
                .build();
    }

    private void extractWeatherButton(GuiGraphicsExtractor graphics,
                                      @NonNull Button button,
                                      WeatherValue value,
                                      String iconName,
                                      int x,
                                      int y,
                                      int mouseX,
                                      int mouseY,
                                      float a) {
        button.setPosition(x, y);
        button.setFocused(configuration.visuals().getWeatherValue() == value);
        button.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.blit(GUI_TEXTURED, fromNamespaceAndPath(MOD_ID, "textures/gui/sprites/weather/" + iconName + ".png"), x + button.getWidth() / 2 - ICON_SIZE / 2, y + button.getHeight() / 2 - ICON_SIZE / 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }
}
