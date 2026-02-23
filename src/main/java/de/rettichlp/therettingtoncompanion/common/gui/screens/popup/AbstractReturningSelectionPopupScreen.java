package de.rettichlp.therettingtoncompanion.common.gui.screens.popup;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

import static net.minecraft.screen.ScreenTexts.CANCEL;

public abstract class AbstractReturningSelectionPopupScreen<T> extends AbstractReturningPopupScreen<T> {

    public AbstractReturningSelectionPopupScreen(Screen parent, Consumer<T> onClose, Text title) {
        super(parent, onClose, title);
    }

    @Override
    public void initButtons() {
        this.buttonLayout.add(ButtonWidget.builder(CANCEL, button -> close()).width(120).build());
    }
}
