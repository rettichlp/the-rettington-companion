package de.rettichlp.therettingtoncompanion.common.gui.screens.popup;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.ColorButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.util.Formatting;

import java.awt.Color;
import java.util.function.Consumer;

import static java.lang.Integer.parseInt;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.screen.ScreenTexts.CANCEL;
import static net.minecraft.screen.ScreenTexts.OK;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;

public class ColorReturningSelectionPopupScreen extends AbstractReturningSelectionPopupScreen<Color> {

    private final Color fallbackColor;

    private TextFieldWidget colorInputRed;
    private TextFieldWidget colorInputGreen;
    private TextFieldWidget colorInputBlue;

    public ColorReturningSelectionPopupScreen(Screen parent, Consumer<Color> onClose, Color fallbackColor) {
        super(parent, onClose, translatable("trc.option.chat.message_patterns.popup.color.title"));
        this.fallbackColor = fallbackColor;
    }

    @Override
    public void initBody() {
        // minecraft colors
        DirectionalLayoutWidget firstRow = this.layout.add(horizontal().spacing(8));
        DirectionalLayoutWidget secondRow = this.layout.add(horizontal().spacing(8));

        int rowLength = 0;
        for (Formatting value : Formatting.values()) {
            if (!value.isColor()) {
                continue;
            }

            Color color = new Color(value.getColorValue());

            if (rowLength < 8) {
                firstRow.add(new ColorButtonWidget(30, 20, color, this::onReturn));
            } else {
                secondRow.add(new ColorButtonWidget(30, 20, color, this::onReturn));
            }

            rowLength++;
        }

        // custom color
        DirectionalLayoutWidget colorInputRow = this.layout.add(horizontal().spacing(8));

        colorInputRow.add(new TextWidget(literal("R"), this.textRenderer), Positioner::alignVerticalCenter);
        this.colorInputRed = colorInputRow.add(new TextFieldWidget(this.textRenderer, 40, 20, empty()));
        colorInputRow.add(new TextWidget(literal("G"), this.textRenderer), Positioner::alignVerticalCenter);
        this.colorInputGreen = colorInputRow.add(new TextFieldWidget(this.textRenderer, 40, 20, empty()));
        colorInputRow.add(new TextWidget(literal("B"), this.textRenderer), Positioner::alignVerticalCenter);
        this.colorInputBlue = colorInputRow.add(new TextFieldWidget(this.textRenderer, 40, 20, empty()));
    }

    @Override
    public void initButtons() {
        this.buttonLayout.add(ButtonWidget.builder(CANCEL, button -> close()).width(120).build());
        this.buttonLayout.add(ButtonWidget.builder(OK, button -> {
            String redString = this.colorInputRed.getText();
            String greenString = this.colorInputGreen.getText();
            String blueString = this.colorInputBlue.getText();

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
