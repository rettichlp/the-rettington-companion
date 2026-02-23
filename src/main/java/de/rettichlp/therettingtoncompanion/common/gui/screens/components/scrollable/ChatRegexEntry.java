package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.ColorButtonWidget;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.ModOptionScreen;
import de.rettichlp.therettingtoncompanion.common.gui.screens.popup.ColorReturningSelectionPopupScreen;
import de.rettichlp.therettingtoncompanion.common.gui.screens.popup.SoundReturningConfirmationPopupScreen;
import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.Color;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.minecraft.screen.ScreenTexts.OFF;
import static net.minecraft.screen.ScreenTexts.ON;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

public class ChatRegexEntry extends ScrollableListEntry {

    private static final String PRIO_LITERAL = "Prio ";

    private final ChatRegex chatRegex;
    private final boolean editable;

    private final TextFieldWidget textFieldWidgetRegex; // regex input
    private final ButtonWidget buttonWidgetEnabled; // activate/deactivate
    private final ColorButtonWidget buttonWidgetColour; // colour select
    private final ButtonWidget buttonWidgetSoundIdentifier;
    private final TextWidget textWidgetPriority; // priority display
    private final ButtonWidget buttonWidgetPriorityIncrease; // increase priority
    private final ButtonWidget buttonWidgetPriorityDecrease; // decrease priority
    private final ButtonWidget buttonWidgetDelete; // delete

    public ChatRegexEntry(ChatRegex chatRegex, boolean editable) {
        this.chatRegex = chatRegex;
        this.editable = editable;

        ChatRegex defaultChatRegex = configuration.chat().regex().getDefaulChatRegex();

        // TextFieldWidget: Regex
        this.textFieldWidgetRegex = new TextFieldWidget(this.client.textRenderer, 0, 0, 176, 20, empty());
        this.textFieldWidgetRegex.setText(this.editable ? this.chatRegex.getPattern() : this.client.getGameProfile().name());
        this.textFieldWidgetRegex.setEditable(this.editable);

        // Button: Enabled
        boolean enabled = this.editable ? this.chatRegex.isActive() : defaultChatRegex.isActive();
        this.buttonWidgetEnabled = ButtonWidget.builder(enabled ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED), button -> {
            if (this.editable) {
                this.chatRegex.setActive(!enabled);
            } else {
                defaultChatRegex.setActive(!enabled);
            }

            button.setMessage(enabled ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED));
        }).build();
        this.buttonWidgetEnabled.setWidth(30);

        // Button: Colour
        Formatting currentColourValue = this.editable ? this.chatRegex.getColor() : defaultChatRegex.getColor();
        this.buttonWidgetColour = new ColorButtonWidget(20, 20, currentColourValue, formatting -> {
            ColorReturningSelectionPopupScreen colorSelectionPopupScreen = new ColorReturningSelectionPopupScreen(this.client.currentScreen, color -> {
                if (this.editable) {
                    this.chatRegex.setColor(color);
                } else {
                    defaultChatRegex.setColor(color);
                }
            });

            this.client.setScreen(colorSelectionPopupScreen);
        });

        // Button: Sound Identifier
        Identifier currentSoundIdentifierValue = this.editable ? this.chatRegex.getSoundIdentifier() : defaultChatRegex.getSoundIdentifier();
        this.buttonWidgetSoundIdentifier = ButtonWidget.builder(literal("🔊"), button -> {
            SoundReturningConfirmationPopupScreen soundReturningConfirmationPopupScreen = new SoundReturningConfirmationPopupScreen(this.client.currentScreen, identifier -> {
                if (this.editable) {
                    this.chatRegex.setSoundIdentifier(identifier);
                } else {
                    defaultChatRegex.setSoundIdentifier(identifier);
                }
            }, currentSoundIdentifierValue);

            this.client.setScreen(soundReturningConfirmationPopupScreen);
        }).build();
        this.buttonWidgetSoundIdentifier.setWidth(20);

        // Text: Priority
        MutableText priorityLabel = literal(PRIO_LITERAL + this.chatRegex.getPriority());
        this.textWidgetPriority = new TextWidget(priorityLabel, this.client.textRenderer);
        this.client.textRenderer.getWidth(priorityLabel);

        // Button: Priority Increase
        this.buttonWidgetPriorityIncrease = ButtonWidget.builder(literal("+"), button -> {
            this.chatRegex.setPriority(min(9, this.chatRegex.getPriority() + 1));
            this.textWidgetPriority.setMessage(literal(PRIO_LITERAL + this.chatRegex.getPriority()));
        }).build();
        this.buttonWidgetPriorityIncrease.setWidth(20);
        this.buttonWidgetPriorityIncrease.setHeight(10);
        this.buttonWidgetPriorityIncrease.active = this.editable;

        // Button: Priority Decrease
        this.buttonWidgetPriorityDecrease = ButtonWidget.builder(literal("-"), button -> {
            this.chatRegex.setPriority(max(0, this.chatRegex.getPriority() - 1));
            this.textWidgetPriority.setMessage(literal(PRIO_LITERAL + this.chatRegex.getPriority()));
        }).build();
        this.buttonWidgetPriorityDecrease.setWidth(20);
        this.buttonWidgetPriorityDecrease.setHeight(10);
        this.buttonWidgetPriorityDecrease.active = this.editable;

        // Button: Delete
        this.buttonWidgetDelete = ButtonWidget.builder(literal("X").copy().formatted(RED), button -> {
            configuration.chat().regex().getChatRegexes().removeIf(cr -> cr.equals(this.chatRegex));
            this.client.execute(() -> this.client.setScreen(new ModOptionScreen("chat")));
        }).build();
        this.buttonWidgetDelete.setWidth(20);
        this.buttonWidgetDelete.active = this.editable;

        // Add widgets as children
        this.children.add(this.textFieldWidgetRegex);
        this.children.add(this.buttonWidgetEnabled);
        this.children.add(this.buttonWidgetColour);
        this.children.add(this.buttonWidgetSoundIdentifier);
        this.children.add(this.textWidgetPriority);
        this.children.add(this.buttonWidgetPriorityIncrease);
        this.children.add(this.buttonWidgetPriorityDecrease);
        this.children.add(this.buttonWidgetDelete);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        setFocused(null);

        if (this.textFieldWidgetRegex != null && this.editable && this.textFieldWidgetRegex.mouseClicked(click, doubled)) {
            setFocused(this.textFieldWidgetRegex);
            return true;
        }

        if (this.buttonWidgetEnabled != null && this.buttonWidgetEnabled.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.buttonWidgetColour != null && this.buttonWidgetColour.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.buttonWidgetSoundIdentifier != null && this.buttonWidgetSoundIdentifier.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.buttonWidgetPriorityIncrease != null && this.buttonWidgetPriorityIncrease.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.buttonWidgetPriorityDecrease != null && this.buttonWidgetPriorityDecrease.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.buttonWidgetDelete != null && this.buttonWidgetDelete.mouseClicked(click, doubled)) {
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        if (this.textFieldWidgetRegex != null) { // width = 172
            this.textFieldWidgetRegex.setPosition(getContentX(), getContentY());
            this.textFieldWidgetRegex.render(context, mouseX, mouseY, deltaTicks);
            this.textFieldWidgetRegex.setEditableColor(this.chatRegex.isValidPattern() ? Color.WHITE.getRGB() : Color.RED.getRGB());
        }

        if (this.buttonWidgetEnabled != null) { // width = 30
            this.buttonWidgetEnabled.setPosition(getContentX() + 176 + 8, getContentY());
            this.buttonWidgetEnabled.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.buttonWidgetColour != null) { // width = 20
            this.buttonWidgetColour.setPosition(getContentX() + 208 + 8, getContentY());
            this.buttonWidgetColour.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.buttonWidgetSoundIdentifier != null) { // width = 20
            this.buttonWidgetSoundIdentifier.setPosition(getContentX() + 236 + 2, getContentY());
            this.buttonWidgetSoundIdentifier.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.buttonWidgetPriorityIncrease != null) { // width = 20
            this.buttonWidgetPriorityIncrease.setPosition(getContentX() + 258 + 8, getContentY());
            this.buttonWidgetPriorityIncrease.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.buttonWidgetPriorityDecrease != null) { // width = 20
            this.buttonWidgetPriorityDecrease.setPosition(getContentX() + 258 + 8, getContentY() + 10);
            this.buttonWidgetPriorityDecrease.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.textWidgetPriority != null) { // width = variable
            this.textWidgetPriority.setPosition(getContentX() + 286 + 4, getContentMiddleY() - (this.client.textRenderer.fontHeight / 2));
            this.textWidgetPriority.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.buttonWidgetDelete != null) { // width = 20 (calculated from right)
            this.buttonWidgetDelete.setPosition(getContentRightEnd() - 20, getContentY());
            this.buttonWidgetDelete.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.textFieldWidgetRegex != null && this.textFieldWidgetRegex.keyPressed(input)) {
            this.chatRegex.setPattern(this.textFieldWidgetRegex.getText());
            return true;
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (this.textFieldWidgetRegex.keyReleased(input)) {
            this.chatRegex.setPattern(this.textFieldWidgetRegex.getText());
            return true;
        }

        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (this.textFieldWidgetRegex.charTyped(input)) {
            this.chatRegex.setPattern(this.textFieldWidgetRegex.getText());
            return true;
        }

        return super.charTyped(input);
    }
}
