package de.rettichlp.therettingtoncompanion.common.gui.screens.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Comparator.comparingInt;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

public class CompletingTextFieldWidget<T> extends TextFieldWidget {

    private static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().textRenderer;

    private final Collection<T> suggestions;
    private final Function<T, String> toSuggestionString;

    private int highlightedIndex = -1;

    public CompletingTextFieldWidget(int width, int height, Collection<T> suggestions, Function<T, String> toSuggestionString) {
        super(TEXT_RENDERER, width, height, empty());
        this.suggestions = suggestions;
        this.toSuggestionString = toSuggestionString;

        setEditable(true);
    }

    @Override
    public void setChangedListener(Consumer<String> changedListener) {
        super.setChangedListener(changedListener);
    }

    private @NotNull @Unmodifiable Collection<String> getSuggestionsForInput() {
        return getSuggestions().stream()
                .filter(s -> s.toLowerCase().startsWith(getText().toLowerCase()))
                .toList();
    }

    private @NotNull @Unmodifiable Collection<String> getSuggestions() {
        return this.suggestions.stream().map(this.toSuggestionString).toList();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (isFocused()) {
            boolean isArrowDown = input.key() == GLFW_KEY_DOWN;
            if (isArrowDown) {
                this.highlightedIndex = min(this.highlightedIndex + 1, getSuggestionsForInput().size() - 1);
                return true;
            }

            boolean isArrowUp = input.key() == GLFW_KEY_UP;
            if (isArrowUp) {
                this.highlightedIndex = max(this.highlightedIndex - 1, 0);
                return true;
            }

            boolean isEnter = input.key() == GLFW_KEY_ENTER;
            if (isEnter && this.highlightedIndex >= 0 && this.highlightedIndex < getSuggestionsForInput().size()) {
                String selectedSuggestion = new ArrayList<>(getSuggestionsForInput()).get(this.highlightedIndex);
                setText(selectedSuggestion);
                this.highlightedIndex = -1;
                return true;
            }
        }

        return super.keyPressed(input);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (isFocused()) {
            List<String> suggestionsForInput = new ArrayList<>(getSuggestionsForInput());
            int suggestionItemHeight = TEXT_RENDERER.fontHeight + 4;

            int suggestionBoxHeight = suggestionsForInput.size() * suggestionItemHeight;
            context.fill(getX(), getY() + getHeight(), getX() + calculateSuggestionBoxWidth(), getY() + getHeight() + suggestionBoxHeight, new Color(0, 0, 0, 100).getRGB());

            Color color = suggestionsForInput.isEmpty() ? RED : WHITE;
            setEditableColor(color.getRGB());

            suggestionsForInput.forEach(suggestionItem -> {
                int indexOf = suggestionsForInput.indexOf(suggestionItem);
                int yOffset = indexOf * suggestionItemHeight;
                boolean isHighlighted = this.highlightedIndex == indexOf;
                context.drawText(TEXT_RENDERER, literal(suggestionItem), getX() + 2, getY() + getHeight() + 2 + yOffset, (isHighlighted ? YELLOW : WHITE).getRGB(), false);
            });
        }

        super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }

    private int calculateSuggestionBoxWidth() {
        int calculatedWith = getSuggestionsForInput().stream()
                .max(comparingInt(String::length))
                .map(s -> TEXT_RENDERER.getWidth(s) + 4)
                .orElseGet(this::getWidth);

        return max(calculatedWith, getWidth());
    }
}
