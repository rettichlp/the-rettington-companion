package de.rettichlp.therettingtoncompanion.common.gui.screens.popup;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

import static net.minecraft.screen.ScreenTexts.CANCEL;
import static net.minecraft.screen.ScreenTexts.DONE;

public abstract class AbstractReturningConfirmationPopupScreen<T> extends AbstractReturningPopupScreen<T> {

    public AbstractReturningConfirmationPopupScreen(Screen parent, Consumer<T> onClose, Text title) {
        super(parent, onClose, title);
    }

    public abstract T done();

    @Override
    public void initButtons() {
        this.buttonLayout.add(ButtonWidget.builder(CANCEL, button -> close()).width(120).build());
        this.buttonLayout.add(ButtonWidget.builder(DONE, button -> onReturn(done())).width(120).build());
    }
}
