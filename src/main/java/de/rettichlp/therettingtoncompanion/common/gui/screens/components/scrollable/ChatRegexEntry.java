package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable;

import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.MutableText;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static java.lang.Math.max;
import static net.minecraft.screen.ScreenTexts.OFF;
import static net.minecraft.screen.ScreenTexts.ON;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

public class ChatRegexEntry extends ScrollableListEntry {

    private static final String PRIO_LITERAL = "Prio ";

    private final boolean editable;

    private int contentWidth = 0;
    private TextFieldWidget textFieldWidget; // regex input
    private ButtonWidget buttonWidget; // activate/deactivate
    private TextWidget textWidget; // priority display
    private ButtonWidget increasePriorityButton; // increase priority
    private ButtonWidget decreasePriorityButton; // decrease priority

    public ChatRegexEntry(ChatRegex chatRegex, boolean editable) {
        this.editable = editable;

        this.contentWidth += createTextFieldWidget(chatRegex);
        this.contentWidth += 8;
        this.contentWidth += createButtonWidget(chatRegex);
        this.contentWidth += 8;
        this.contentWidth += createTextWidget(chatRegex);
        this.contentWidth += 8;

        if (editable) {
            this.contentWidth += createIncreasePriorityButton(chatRegex);
            this.contentWidth += 2;
            this.contentWidth += createDecreasePriorityButton(chatRegex);
        } else {
            this.contentWidth += 42;
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

        if (this.increasePriorityButton != null && this.increasePriorityButton.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.decreasePriorityButton != null && this.decreasePriorityButton.mouseClicked(click, doubled)) {
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.textFieldWidget != null && this.textFieldWidget.keyPressed(input)) {
            return true;
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (this.textFieldWidget.keyReleased(input)) {
            return true;
        }

        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (this.textFieldWidget.charTyped(input)) {
            return true;
        }

        return super.charTyped(input);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int currentX = getContentMiddleX() - this.contentWidth / 2;

        if (this.textFieldWidget != null) {
            this.textFieldWidget.setPosition(currentX, getContentY());
            this.textFieldWidget.render(context, mouseX, mouseY, deltaTicks);
            currentX = currentX + this.textFieldWidget.getWidth() + 8;
        }

        if (this.buttonWidget != null) {
            this.buttonWidget.setPosition(currentX, getContentY());
            this.buttonWidget.render(context, mouseX, mouseY, deltaTicks);
            currentX = currentX + this.buttonWidget.getWidth() + 8;
        }

        if (this.textWidget != null) {
            this.textWidget.setPosition(currentX, getContentMiddleY() - (this.client.textRenderer.fontHeight / 2));
            this.textWidget.render(context, mouseX, mouseY, deltaTicks);
            currentX = currentX + this.textWidget.getWidth() + 8;
        }

        if (this.increasePriorityButton != null) {
            this.increasePriorityButton.setPosition(currentX, getContentY());
            this.increasePriorityButton.render(context, mouseX, mouseY, deltaTicks);
            currentX = currentX + this.increasePriorityButton.getWidth() + 2;
        }

        if (this.decreasePriorityButton != null) {
            this.decreasePriorityButton.setPosition(currentX, getContentY());
            this.decreasePriorityButton.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    private int createTextFieldWidget(ChatRegex chatRegex) {
        this.textFieldWidget = new TextFieldWidget(this.client.textRenderer, 0, 0, 150, 20, empty());
        this.textFieldWidget.setText(chatRegex.getPattern().pattern());
        this.textFieldWidget.setEditable(this.editable);
        return 150;
    }

    private int createButtonWidget(ChatRegex chatRegex) {
        if (this.editable) {
            this.buttonWidget = ButtonWidget.builder(chatRegex.isActive() ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED), button -> {
                chatRegex.setActive(!chatRegex.isActive());
                button.setMessage(chatRegex.isActive() ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED));
            }).build();
        } else {
            this.buttonWidget = ButtonWidget.builder(configuration.isDefaultChatRegex() ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED), button -> {
                configuration.setDefaultChatRegex(!configuration.isDefaultChatRegex());
                button.setMessage(configuration.isDefaultChatRegex() ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED));
            }).build();
        }

        this.buttonWidget.setWidth(50);
        return 50;
    }

    private int createTextWidget(ChatRegex chatRegex) {
        MutableText priorityLabel = literal(PRIO_LITERAL + chatRegex.getPriority());
        this.textWidget = new TextWidget(priorityLabel, this.client.textRenderer);
        return this.client.textRenderer.getWidth(priorityLabel);
    }

    private int createIncreasePriorityButton(ChatRegex chatRegex) {
        this.increasePriorityButton = ButtonWidget.builder(literal("+"), button -> {
            chatRegex.setPriority(chatRegex.getPriority() + 1);
            this.textWidget.setMessage(literal(PRIO_LITERAL + chatRegex.getPriority()));
        }).build();
        this.increasePriorityButton.setWidth(20);
        return 20;
    }

    private int createDecreasePriorityButton(ChatRegex chatRegex) {
        this.decreasePriorityButton = ButtonWidget.builder(literal("-"), button -> {
            chatRegex.setPriority(max(0, chatRegex.getPriority() - 1));
            this.textWidget.setMessage(literal(PRIO_LITERAL + chatRegex.getPriority()));
        }).build();
        this.decreasePriorityButton.setWidth(20);
        return 20;
    }
}
