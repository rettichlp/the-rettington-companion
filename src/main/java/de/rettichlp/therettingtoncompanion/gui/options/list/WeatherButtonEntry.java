package de.rettichlp.therettingtoncompanion.gui.options.list;

import de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration;
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
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.WeatherValue.W_CLEAR;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.WeatherValue.W_RAIN;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.WeatherValue.W_THUNDER;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class WeatherButtonEntry extends AbstractEntry {

    private final StringWidget stringWidget;
    private final Button sunButton;
    private final Button rainButton;
    private final Button thunderButton;

    protected WeatherButtonEntry(Font font,
                                 Component label,
                                 Tooltip tooltip,
                                 Consumer<VisualsConfiguration.WeatherValue> valueChangeListener) {
        this.stringWidget = new StringWidget(label, font);
        this.stringWidget.setTooltip(tooltip);
        this.sunButton = Button.builder(empty(), _ -> valueChangeListener.accept(W_CLEAR))
                .size(32, 20)
                .build();
        this.rainButton = Button.builder(empty(), _ -> valueChangeListener.accept(W_RAIN))
                .size(32, 20)
                .build();
        this.thunderButton = Button.builder(empty(), _ -> valueChangeListener.accept(W_THUNDER))
                .size(32, 20)
                .build();
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.stringWidget.setPosition(getContentX(), getContentYMiddle() - this.stringWidget.getHeight() / 2);
        this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);

        int sunButtonX = getContentRight() - this.sunButton.getWidth() + 2 - 68;
        int sunButtonY = getContentYMiddle() - this.sunButton.getHeight() / 2;
        this.sunButton.setPosition(sunButtonX, sunButtonY);
        this.sunButton.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.blit(GUI_TEXTURED, fromNamespaceAndPath(MOD_ID, "textures/gui/sprites/weather/sun.png"), sunButtonX + 10, sunButtonY + 4, 0, 0, 12, 12, 12, 12);

        int rainButtonX = getContentRight() - this.rainButton.getWidth() + 2 - 34;
        int rainButtonY = getContentYMiddle() - this.rainButton.getHeight() / 2;
        this.rainButton.setPosition(rainButtonX, rainButtonY);
        this.rainButton.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.blit(GUI_TEXTURED, fromNamespaceAndPath(MOD_ID, "textures/gui/sprites/weather/rain.png"), rainButtonX + 10, rainButtonY + 4, 0, 0, 12, 12, 12, 12);

        int thunderButtonX = getContentRight() - this.thunderButton.getWidth() + 2;
        int thunderButtonY = getContentYMiddle() - this.thunderButton.getHeight() / 2;
        this.thunderButton.setPosition(thunderButtonX, thunderButtonY);
        this.thunderButton.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.blit(GUI_TEXTURED, fromNamespaceAndPath(MOD_ID, "textures/gui/sprites/weather/thunder.png"), thunderButtonX + 10, thunderButtonY + 4, 0, 0, 12, 12, 12, 12);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.stringWidget, this.sunButton, this.rainButton, this.thunderButton);
    }
}
