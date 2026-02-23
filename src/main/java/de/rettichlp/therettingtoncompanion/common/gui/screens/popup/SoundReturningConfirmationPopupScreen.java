package de.rettichlp.therettingtoncompanion.common.gui.screens.popup;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static net.minecraft.registry.Registries.SOUND_EVENT;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.translatable;

public class SoundReturningConfirmationPopupScreen extends AbstractReturningConfirmationPopupScreen<Identifier> {

    private final Identifier initialValue;

    private TextFieldWidget textFieldWidget;

    public SoundReturningConfirmationPopupScreen(Screen parent, Consumer<Identifier> onClose, Identifier initialValue) {
        super(parent, onClose, translatable("trc.option.chat.message_patterns.popup.sound.title"));
        this.initialValue = initialValue;
    }

    @Override
    public void initBody() {
        this.textFieldWidget = new TextFieldWidget(this.client.textRenderer, 0, 0, 248, 20, empty());
        this.textFieldWidget.setMaxLength(256);
        this.textFieldWidget.setText(this.initialValue.toString());

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
