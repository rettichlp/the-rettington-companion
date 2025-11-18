package de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable;

import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;

import static java.lang.Math.max;
import static net.minecraft.screen.ScreenTexts.OFF;
import static net.minecraft.screen.ScreenTexts.ON;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

public class ChatRegexEntry extends ScrollableListEntry {

    private final TextFieldWidget textFieldWidget; // regex input
    private final ButtonWidget buttonWidget; // activate/deactivate
    private final TextWidget textWidget; // priority display
    private final ButtonWidget increasePriorityButton; // increase priority
    private final ButtonWidget decreasePriorityButton; // decrease priority

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        setFocused(null);

        if (this.textFieldWidget.mouseClicked(click, doubled)) {
            setFocused(this.textFieldWidget);
            return true;
        }

        if (this.buttonWidget.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.increasePriorityButton.mouseClicked(click, doubled)) {
            return true;
        }

        if (this.decreasePriorityButton.mouseClicked(click, doubled)) {
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.textFieldWidget.keyPressed(input)) {
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

    public ChatRegexEntry(ChatRegex chatRegex) {
        this.textFieldWidget = new TextFieldWidget(this.client.textRenderer, 0, 0, 150, 20, empty());
        this.textFieldWidget.setText(chatRegex.getPattern().pattern());
        this.textFieldWidget.setEditable(true);

        this.buttonWidget = ButtonWidget.builder(chatRegex.isActive() ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED), button -> {
            chatRegex.setActive(!chatRegex.isActive());
            button.setMessage(chatRegex.isActive() ? ON.copy().formatted(GREEN) : OFF.copy().formatted(RED));
        }).build();
        this.buttonWidget.setWidth(50);

        this.textWidget = new TextWidget(literal("Prio " + chatRegex.getPriority()), this.client.textRenderer);

        this.increasePriorityButton = ButtonWidget.builder(literal("+"), button -> {
            chatRegex.setPriority(chatRegex.getPriority() + 1);
            this.textWidget.setMessage(literal("Prio " + chatRegex.getPriority()));
        }).build();
        this.increasePriorityButton.setWidth(20);

        this.decreasePriorityButton = ButtonWidget.builder(literal("-"), button -> {
            chatRegex.setPriority(max(0, chatRegex.getPriority() - 1));
            this.textWidget.setMessage(literal("Prio " + chatRegex.getPriority()));
        }).build();
        this.decreasePriorityButton.setWidth(20);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int currentX = getContentMiddleX() - ((this.textFieldWidget.getWidth() + this.buttonWidget.getWidth() + this.textWidget.getWidth() + this.increasePriorityButton.getWidth() + this.decreasePriorityButton.getWidth() + 26) / 2);

        this.textFieldWidget.setPosition(currentX, getContentY());
        this.textFieldWidget.render(context, mouseX, mouseY, deltaTicks);
        currentX = currentX + this.textFieldWidget.getWidth() + 8;

        this.buttonWidget.setPosition(currentX, getContentY());
        this.buttonWidget.render(context, mouseX, mouseY, deltaTicks);
        currentX = currentX + this.buttonWidget.getWidth() + 8;

        this.textWidget.setPosition(currentX, getContentMiddleY() - (this.client.textRenderer.fontHeight / 2));
        this.textWidget.render(context, mouseX, mouseY, deltaTicks);
        currentX = currentX + this.textWidget.getWidth() + 8;

        this.increasePriorityButton.setPosition(currentX, getContentY());
        this.increasePriorityButton.render(context, mouseX, mouseY, deltaTicks);
        currentX = currentX + this.increasePriorityButton.getWidth() + 2;

        this.decreasePriorityButton.setPosition(currentX, getContentY());
        this.decreasePriorityButton.render(context, mouseX, mouseY, deltaTicks);
    }
}
