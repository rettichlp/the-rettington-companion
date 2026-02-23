package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.ColorSelectWidget;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.ModOptionScreen;
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

    private TextFieldWidget textFieldWidget; // regex input
    private ButtonWidget buttonWidget; // activate/deactivate
    private ColorSelectWidget colorSelectWidget; // color select
    private ButtonWidget buttonSoundWidget;
    private TextWidget textWidget; // priority display
    private ButtonWidget increasePriorityButton; // increase priority
    private ButtonWidget decreasePriorityButton; // decrease priority
    private ButtonWidget deleteButtonWidget; // delete

    public ChatRegexEntry(ChatRegex chatRegex, boolean editable) {
        this.chatRegex = chatRegex;
        this.editable = editable;

        createTextFieldWidget();
        createButtonWidget();
        createColorSelectWidget();
        createButtonSoundWidget();
        createTextWidget();

        if (editable) {
            createIncreasePriorityButton();
            createDecreasePriorityButton();
            createDeleteButtonWidget();
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        setFocused(null);

        if (this.textFieldWidget != null && this.editable && this.textFieldWidget.mouseClicked(click, doubled)) {
            setFocused(this.textFieldWidget);
            return true;
        }

        if (this.buttonWidget != null && this.buttonWidget.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.colorSelectWidget != null && this.colorSelectWidget.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.buttonSoundWidget != null && this.buttonSoundWidget.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.increasePriorityButton != null && this.increasePriorityButton.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.decreasePriorityButton != null && this.decreasePriorityButton.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.deleteButtonWidget != null && this.deleteButtonWidget.mouseClicked(click, doubled)) {
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        if (this.textFieldWidget != null) { // width = 136
            this.textFieldWidget.setPosition(getContentX(), getContentY());
            this.textFieldWidget.render(context, mouseX, mouseY, deltaTicks);
            this.textFieldWidget.setEditableColor(this.chatRegex.isValidPattern() ? Color.WHITE.getRGB() : Color.RED.getRGB());
        }

        if (this.buttonWidget != null) { // width = 30
            this.buttonWidget.setPosition(getContentX() + 172 + 8, getContentY());
            this.buttonWidget.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.colorSelectWidget != null) { // width = 30
            this.colorSelectWidget.setPosition(getContentX() + 268 + 8, getContentY());
            this.colorSelectWidget.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.buttonSoundWidget != null) { // width = 20
            this.buttonSoundWidget.setPosition(getContentX() + 232 + 2, getContentY());
            this.buttonSoundWidget.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.textWidget != null) { // width = variable (calculated from right)
            this.textWidget.setPosition(getContentRightEnd() - this.client.textRenderer.getWidth(this.textWidget.getMessage()) - 56, getContentMiddleY() - (this.client.textRenderer.fontHeight / 2));
            this.textWidget.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.increasePriorityButton != null) { // width = 20 (calculated from right)
            this.increasePriorityButton.setPosition(getContentRightEnd() - 48, getContentY());
            this.increasePriorityButton.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.decreasePriorityButton != null) { // width = 20 (calculated from right)
            this.decreasePriorityButton.setPosition(getContentRightEnd() - 48, getContentY() + 10);
            this.decreasePriorityButton.render(context, mouseX, mouseY, deltaTicks);
        }

        if (this.deleteButtonWidget != null) { // width = 20 (calculated from right)
            this.deleteButtonWidget.setPosition(getContentRightEnd() - 20, getContentY());
            this.deleteButtonWidget.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.colorSelectWidget.isHovered() && this.colorSelectWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.textFieldWidget != null && this.textFieldWidget.keyPressed(input)) {
            this.chatRegex.setPattern(this.textFieldWidget.getText());
            return true;
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (this.textFieldWidget.keyReleased(input)) {
            this.chatRegex.setPattern(this.textFieldWidget.getText());
            return true;
        }

        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (this.textFieldWidget.charTyped(input)) {
            this.chatRegex.setPattern(this.textFieldWidget.getText());
            return true;
        }

        return super.charTyped(input);
    }

    private void createTextFieldWidget() {
        this.textFieldWidget = new TextFieldWidget(this.client.textRenderer, 0, 0, 172, 20, empty());
        this.textFieldWidget.setText(this.editable ? this.chatRegex.getPattern() : this.client.getGameProfile().name());
        this.textFieldWidget.setEditable(this.editable);

        this.children.add(this.textFieldWidget);
    }

    private void createButtonWidget() {
        if (this.editable) {
            this.buttonWidget = ButtonWidget.builder(this.chatRegex.isActive() ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED), button -> {
                this.chatRegex.setActive(!this.chatRegex.isActive());
                button.setMessage(this.chatRegex.isActive() ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED));
            }).build();
        } else {
            ChatRegex defaulChatRegex = configuration.chat().regex().getDefaulChatRegex();
            boolean defaultChatRegexActive = defaulChatRegex.isActive();
            this.buttonWidget = ButtonWidget.builder(defaultChatRegexActive ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED), button -> {
                defaulChatRegex.setActive(!defaultChatRegexActive);
                button.setMessage(defaultChatRegexActive ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED));
            }).build();
        }

        this.buttonWidget.setWidth(30);

        this.children.add(this.buttonWidget);
    }

    private void createColorSelectWidget() {
        ChatRegex defaulChatRegex = configuration.chat().regex().getDefaulChatRegex();
        this.colorSelectWidget = this.editable
                ? new ColorSelectWidget(20, 20, this.chatRegex.getColor(), this.chatRegex::setColor)
                : new ColorSelectWidget(20, 20, defaulChatRegex.getColor(), defaulChatRegex::setColor);
        this.colorSelectWidget.setWidth(30);

        this.children.add(this.colorSelectWidget);
    }

    private void createButtonSoundWidget() {
        ChatRegex defaulChatRegex = configuration.chat().regex().getDefaulChatRegex();
        Identifier currentValue = this.editable ? this.chatRegex.getSoundIdentifier() : defaulChatRegex.getSoundIdentifier();

        this.buttonSoundWidget = ButtonWidget.builder(literal("🔊"), button -> {
            SoundReturningConfirmationPopupScreen soundReturningConfirmationPopupScreen = new SoundReturningConfirmationPopupScreen(this.client.currentScreen, identifier -> {
                if (this.editable) {
                    this.chatRegex.setSoundIdentifier(identifier);
                } else {
                    defaulChatRegex.setSoundIdentifier(identifier);
                }
            }, currentValue);

            this.client.setScreen(soundReturningConfirmationPopupScreen);
        }).build();

        this.buttonSoundWidget.setWidth(20);

        this.children.add(this.buttonSoundWidget);
    }

    private void createTextWidget() {
        MutableText priorityLabel = literal(PRIO_LITERAL + this.chatRegex.getPriority());
        this.textWidget = new TextWidget(priorityLabel, this.client.textRenderer);
        this.client.textRenderer.getWidth(priorityLabel);

        this.children.add(this.textWidget);
    }

    private void createIncreasePriorityButton() {
        this.increasePriorityButton = ButtonWidget.builder(literal("+"), button -> {
            this.chatRegex.setPriority(min(9, this.chatRegex.getPriority() + 1));
            this.textWidget.setMessage(literal(PRIO_LITERAL + this.chatRegex.getPriority()));
        }).build();
        this.increasePriorityButton.setWidth(20);
        this.increasePriorityButton.setHeight(10);

        this.children.add(this.increasePriorityButton);
    }

    private void createDecreasePriorityButton() {
        this.decreasePriorityButton = ButtonWidget.builder(literal("-"), button -> {
            this.chatRegex.setPriority(max(0, this.chatRegex.getPriority() - 1));
            this.textWidget.setMessage(literal(PRIO_LITERAL + this.chatRegex.getPriority()));
        }).build();
        this.decreasePriorityButton.setWidth(20);
        this.decreasePriorityButton.setHeight(10);

        this.children.add(this.decreasePriorityButton);
    }

    private void createDeleteButtonWidget() {
        this.deleteButtonWidget = ButtonWidget.builder(literal("X").copy().formatted(RED), button -> {
            configuration.chat().regex().getChatRegexes().removeIf(cr -> cr.equals(this.chatRegex));
            this.client.execute(() -> this.client.setScreen(new ModOptionScreen("chat")));
        }).build();
        this.deleteButtonWidget.setWidth(20);

        this.children.add(this.deleteButtonWidget);
    }
}
