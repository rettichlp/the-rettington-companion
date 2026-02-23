package de.rettichlp.therettingtoncompanion.common.gui.screens.popup;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.function.Consumer;

import static net.minecraft.screen.ScreenTexts.CANCEL;

public abstract class AbstractReturningSelectionPopupScreen<T> extends AbstractReturningPopupScreen<T> {

    public AbstractReturningSelectionPopupScreen(Screen parent, Consumer<T> onClose) {
        super(parent, onClose);
    }

    @Override
    public void initButtons() {
        this.buttonLayout.add(ButtonWidget.builder(CANCEL, button -> close()).width(120).build());
    }
}
