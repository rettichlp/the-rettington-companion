package de.rettichlp.therettingtoncompanion.gui.options.list;

import de.rettichlp.therettingtoncompanion.gui.ColorButton;
import de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry;
import de.rettichlp.therettingtoncompanion.gui.PatternEditBox;
import de.rettichlp.therettingtoncompanion.gui.screens.ColorSelectionPopupScreen;
import de.rettichlp.therettingtoncompanion.gui.screens.SoundSelectionPopupScreen;
import de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen;
import lombok.Data;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.OFF;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static de.rettichlp.therettingtoncompanion.gui.screens.SoundSelectionPopupScreen.isValidSoundIdentifier;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.isValidPattern;
import static java.awt.Color.GREEN;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Comparator.comparingInt;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.resources.Identifier.parse;
import static net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL;

public class FilteredMessageEntry extends AbstractEntry {

    private static final String PRIO_LITERAL = "Prio ";

    private final Minecraft minecraft = Minecraft.getInstance();

    private final FilteredMessage filteredMessage;
    private final boolean editable;

    private final PatternEditBox patternEditBox; // regex input
    private final CycleButton<OnOffCycleButtonEntry> toggleButton; // activate/deactivate
    private final ColorButton colorButton; // colour select (not final and null for dynamic color update)
    private final Button soundSelectionButton;
    private final StringWidget stringWidgetPriority; // priority display
    private final Button priorityIncreaseButton; // increase priority
    private final Button priorityDecreaseButton; // decrease priority
    private final Button deleteButton; // delete

    protected FilteredMessageEntry(FilteredMessageEntry.FilteredMessage filteredMessage, boolean editable) {
        FilteredMessage defaultFilteredMessage = configuration.chat().filteredMessage().getDefaultFilteredMessage();

        this.filteredMessage = filteredMessage;
        this.editable = editable;

        this.patternEditBox = new PatternEditBox(this.minecraft.font, this.editable ? this.filteredMessage.getPatternString() : this.minecraft.getGameProfile().name(), this.filteredMessage::setPatternString);
        this.patternEditBox.setEditable(this.editable);

        boolean enabled = this.editable ? this.filteredMessage.isActive() : defaultFilteredMessage.isActive();
        this.toggleButton = CycleButton.builder(OnOffCycleButtonEntry::value, enabled ? ON : OFF)
                .withValues(OnOffCycleButtonEntry.values())
                .displayOnlyValue()
                .create(0, 0, 30, 20, empty(), (_, value) -> (this.editable ? this.filteredMessage : defaultFilteredMessage).setActive(value == ON));

        this.colorButton = new ColorButton(0, 0, 30, 20, this.filteredMessage.getColor(), button -> this.minecraft.gui.setScreen(new ColorSelectionPopupScreen(this.minecraft.gui.screen(), this.filteredMessage.getColor(), color -> {
            (this.editable ? this.filteredMessage : defaultFilteredMessage).setColor(color);
            ((ColorButton) button).setColor(color);
        })));

        this.soundSelectionButton = Button.builder(literal("🔊"), _ -> this.minecraft.gui.setScreen(new SoundSelectionPopupScreen(this.minecraft.gui.screen(), this.filteredMessage.getSoundIdentifier(), (this.editable ? this.filteredMessage : defaultFilteredMessage)::setSoundIdentifierString)))
                .width(30)
                .build();

        this.stringWidgetPriority = new StringWidget(literal(PRIO_LITERAL + this.filteredMessage.getPriority()), this.minecraft.font);
        this.stringWidgetPriority.setWidth(this.minecraft.font.width(this.stringWidgetPriority.getMessage()));

        this.priorityIncreaseButton = Button.builder(literal("+"), _ -> {
            this.filteredMessage.setPriority(min(9, this.filteredMessage.getPriority() + 1));
            this.stringWidgetPriority.setMessage(literal(PRIO_LITERAL + this.filteredMessage.getPriority()));
        }).build();
        this.priorityIncreaseButton.setWidth(20);
        this.priorityIncreaseButton.setHeight(10);
        this.priorityIncreaseButton.active = this.editable;

        this.priorityDecreaseButton = Button.builder(literal("-"), _ -> {
            this.filteredMessage.setPriority(max(0, this.filteredMessage.getPriority() - 1));
            this.stringWidgetPriority.setMessage(literal(PRIO_LITERAL + this.filteredMessage.getPriority()));
        }).build();
        this.priorityDecreaseButton.setWidth(20);
        this.priorityDecreaseButton.setHeight(10);
        this.priorityDecreaseButton.active = this.editable;

        this.deleteButton = Button.builder(literal("X").withColor(TextColor.RED), _ -> {
            configuration.chat().filteredMessage().getFilteredMessages().removeIf(cr -> cr.equals(this.filteredMessage));
            this.minecraft.gui.setScreen(new TRCOptionsScreen("chat", new PauseScreen(true), true));
        }).build();
        this.deleteButton.setWidth(20);
        this.deleteButton.active = this.editable;
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.patternEditBox.setWidth(getContentWidth() / 2);
        this.patternEditBox.setPosition(getContentX(), getContentYMiddle() - this.patternEditBox.getHeight() / 2);
        this.patternEditBox.extractRenderState(graphics, mouseX, mouseY, a);

        this.toggleButton.setPosition(this.patternEditBox.getX() + this.patternEditBox.getWidth() + 8, getContentYMiddle() - this.toggleButton.getHeight() / 2);
        this.toggleButton.extractRenderState(graphics, mouseX, mouseY, a);

        this.colorButton.setPosition(this.toggleButton.getX() + this.toggleButton.getWidth() + 8, getContentYMiddle() - this.colorButton.getHeight() / 2);
        this.colorButton.extractRenderState(graphics, mouseX, mouseY, a);

        this.soundSelectionButton.setPosition(this.colorButton.getX() + this.colorButton.getWidth() + 8, getContentYMiddle() - this.soundSelectionButton.getHeight() / 2);
        this.soundSelectionButton.extractRenderState(graphics, mouseX, mouseY, a);

        this.stringWidgetPriority.setPosition(this.priorityIncreaseButton.getX() - 8 - this.stringWidgetPriority.getWidth(), getContentYMiddle() - this.stringWidgetPriority.getHeight() / 2);
        this.stringWidgetPriority.extractRenderState(graphics, mouseX, mouseY, a);

        this.priorityIncreaseButton.setPosition(this.deleteButton.getX() - 8 - this.priorityIncreaseButton.getWidth(), getContentY());
        this.priorityIncreaseButton.extractRenderState(graphics, mouseX, mouseY, a);

        this.priorityDecreaseButton.setPosition(this.deleteButton.getX() - 8 - this.priorityDecreaseButton.getWidth(), getContentYMiddle());
        this.priorityDecreaseButton.extractRenderState(graphics, mouseX, mouseY, a);

        this.deleteButton.setPosition(getContentRight() - this.deleteButton.getWidth(), getContentYMiddle() - this.deleteButton.getHeight() / 2);
        this.deleteButton.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.patternEditBox, this.toggleButton, this.colorButton, this.soundSelectionButton, this.stringWidgetPriority, this.priorityIncreaseButton, this.priorityDecreaseButton, this.deleteButton);
    }

    @Data
    public static class FilteredMessage {

        private static final SoundEvent DEFAULT_SOUND = NOTE_BLOCK_BELL.value();

        private String patternString;
        private String soundIdentifierString;
        @Getter
        private boolean active;
        private int colorValue;
        @Getter
        private int priority;

        public FilteredMessage(String patternString) {
            this.patternString = patternString;
            this.soundIdentifierString = DEFAULT_SOUND.location().toString();
            this.active = true;
            this.colorValue = GREEN.getRGB();
            this.priority = 5;
        }

        public Optional<Pattern> getPattern() {
            return isValidPattern(this.patternString)
                    ? Optional.of(compile("^" + this.patternString + "$", CASE_INSENSITIVE))
                    : Optional.empty();
        }

        public Identifier getSoundIdentifier() {
            return isValidSoundIdentifier(this.soundIdentifierString) ? parse(this.soundIdentifierString) : DEFAULT_SOUND.location();
        }

        public Color getColor() {
            return new Color(this.colorValue);
        }

        public void setColor(@NonNull Color color) {
            this.colorValue = color.getRGB();
        }

        public static @Nullable FilteredMessage getBestMatchingFilteredMessage(String message) {
            // matches default regex
            FilteredMessage defaultFilteredMessage = configuration.chat().filteredMessage().getDefaultFilteredMessage();
            if (matchesDefaultFilteredMessage(defaultFilteredMessage, message)) {
                return defaultFilteredMessage;
            }

            // matches custom regexes
            List<FilteredMessage> matchingCustomRegexes = getMatchingCustomRegexes(message);
            return matchingCustomRegexes.isEmpty() ? null : matchingCustomRegexes.getFirst();
        }

        private static boolean matchesDefaultFilteredMessage(@NonNull FilteredMessage defaultFilteredMessage, String message) {
            return defaultFilteredMessage.isActive() && message.toLowerCase().contains(player.getGameProfile().name().toLowerCase());
        }

        private static @NonNull @Unmodifiable List<FilteredMessage> getMatchingCustomRegexes(CharSequence message) {
            return configuration.chat().filteredMessage().getFilteredMessages().stream()
                    .filter(FilteredMessage::isActive)
                    .filter(filteredMessage -> filteredMessage.getPattern().map(pattern -> pattern.matcher(message).find()).orElse(false))
                    .sorted(comparingInt(FilteredMessage::getPriority).reversed())
                    .toList();
        }
    }
}
