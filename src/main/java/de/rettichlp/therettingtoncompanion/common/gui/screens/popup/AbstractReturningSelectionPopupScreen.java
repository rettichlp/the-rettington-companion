package de.rettichlp.therettingtoncompanion.common.gui.screens.popup;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

import static net.minecraft.network.chat.CommonComponents.GUI_CANCEL;

public abstract class AbstractReturningSelectionPopupScreen<T> extends AbstractReturningPopupScreen<T> {

    public AbstractReturningSelectionPopupScreen(Screen parent, Consumer<T> onClose, Component title) {
        super(parent, onClose, title);
    }

    @Override
    public void initButtons() {
        this.buttonLayout.addChild(Button.builder(GUI_CANCEL, button -> close()).width(120).build());
    }
}
