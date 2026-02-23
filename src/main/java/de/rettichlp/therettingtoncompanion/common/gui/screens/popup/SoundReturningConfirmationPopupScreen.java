package de.rettichlp.therettingtoncompanion.common.gui.screens.popup;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.util.function.Consumer;

import static java.awt.Color.*;
import static net.minecraft.registry.Registries.SOUND_EVENT;
import static net.minecraft.text.Text.empty;

public class SoundReturningConfirmationPopupScreen extends AbstractReturningConfirmationPopupScreen<Identifier> {

    private final Identifier initialValue;

    private TextFieldWidget textFieldWidget;

    public SoundReturningConfirmationPopupScreen(Screen parent, Consumer<Identifier> onClose, Identifier initialValue) {
        super(parent, onClose);
        this.initialValue = initialValue;
    }

    @Override
    public void initBody() {
        this.textFieldWidget = new TextFieldWidget(this.client.textRenderer, 0, 0, 200, 20, empty());
        this.textFieldWidget.setText(this.initialValue.toString());
        this.textFieldWidget.setMaxLength(200);

        this.layout.add(this.textFieldWidget);
    }

    @Override
    public Identifier done() {
        String newValue = this.textFieldWidget.getText();
        return validSoundIdentifier(newValue) ? Identifier.of(newValue) : this.initialValue;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        this.textFieldWidget.setEditableColor(validSoundIdentifier(this.textFieldWidget.getText()) ? WHITE.getRGB() : RED.getRGB());
    }

    private boolean validSoundIdentifier(String soundIdentifier) {
        return SOUND_EVENT.containsId(Identifier.of(soundIdentifier));
    }
}
