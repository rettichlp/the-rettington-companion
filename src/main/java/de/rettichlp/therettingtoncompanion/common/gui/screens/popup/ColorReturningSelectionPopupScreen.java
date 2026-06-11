package de.rettichlp.therettingtoncompanion.common.gui.screens.popup;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.ColorButtonWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;

import java.awt.Color;
import java.util.function.Consumer;

import static java.lang.Integer.parseInt;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.network.chat.CommonComponents.GUI_CANCEL;
import static net.minecraft.network.chat.CommonComponents.GUI_OK;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

public class ColorReturningSelectionPopupScreen extends AbstractReturningSelectionPopupScreen<Color> {

    private final Color fallbackColor;

    private StringWidget colorInputRed;
    private StringWidget colorInputGreen;
    private StringWidget colorInputBlue;

    public ColorReturningSelectionPopupScreen(Screen parent, Consumer<Color> onClose, Color fallbackColor) {
        super(parent, onClose, translatable("trc.option.chat.message_patterns.popup.color.title"));
        this.fallbackColor = fallbackColor;
    }

    @Override
    public void initBody() {
        // minecraft colors
        LinearLayout firstRow = this.layout.addChild(horizontal().spacing(8));
        LinearLayout secondRow = this.layout.addChild(horizontal().spacing(8));

        int rowLength = 0;
        for (ChatFormatting value : ChatFormatting.values()) {
            if (!value.isColor()) {
                continue;
            }

            Color color = new Color(value.getColor());

            if (rowLength < 8) {
                firstRow.addChild(new ColorButtonWidget(30, 20, color, this::onReturn));
            } else {
                secondRow.addChild(new ColorButtonWidget(30, 20, color, this::onReturn));
            }

            rowLength++;
        }

        // custom color
        LinearLayout colorInputRow = this.layout.addChild(horizontal().spacing(8));

        colorInputRow.addChild(new StringWidget(literal("R"), this.font), LayoutSettings::alignVerticallyMiddle);
        this.colorInputRed = colorInputRow.addChild(new StringWidget(40, 20, empty(), this.font));
        colorInputRow.addChild(new StringWidget(literal("G"), this.font), LayoutSettings::alignVerticallyMiddle);
        this.colorInputGreen = colorInputRow.addChild(new StringWidget(40, 20, empty(), this.font));
        colorInputRow.addChild(new StringWidget(literal("B"), this.font), LayoutSettings::alignVerticallyMiddle);
        this.colorInputBlue = colorInputRow.addChild(new StringWidget(40, 20, empty(), this.font));
    }

    @Override
    public void initButtons() {
        this.buttonLayout.addChild(Button.builder(GUI_CANCEL, button -> onClose()).width(120).build());
        this.buttonLayout.addChild(Button.builder(GUI_OK, button -> {
            String redString = this.colorInputRed.getMessage().getString();
            String greenString = this.colorInputGreen.getMessage().getString();
            String blueString = this.colorInputBlue.getMessage().getString();

            Color color;
            try {
                int red = parseInt(redString);
                int green = parseInt(greenString);
                int blue = parseInt(blueString);
                color = new Color(red, green, blue);
            } catch (NumberFormatException e) {
                color = this.fallbackColor;
            }

            onReturn(color);
        }).width(120).build());
    }
}
