package de.rettichlp.therettingtoncompanion.common.gui.screens.popup;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.ColorButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;

public class ColorReturningSelectionPopupScreen extends AbstractReturningSelectionPopupScreen<Formatting> {

    public ColorReturningSelectionPopupScreen(Screen parent, Consumer<Formatting> onClose) {
        super(parent, onClose);
    }

    @Override
    public void initBody() {
        DirectionalLayoutWidget firstRow = this.layout.add(horizontal().spacing(8));
        DirectionalLayoutWidget secondRow = this.layout.add(horizontal().spacing(8));

        int rowLength = 0;
        for (Formatting value : Formatting.values()) {
            if (!value.isColor()) {
                continue;
            }

            if (rowLength < 8) {
                firstRow.add(new ColorButtonWidget(30, 20, value, this::onReturn));
            } else {
                secondRow.add(new ColorButtonWidget(30, 20, value, this::onReturn));
            }

            rowLength++;
        }
    }
}
