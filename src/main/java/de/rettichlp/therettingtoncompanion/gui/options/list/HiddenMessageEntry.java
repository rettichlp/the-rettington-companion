package de.rettichlp.therettingtoncompanion.gui.options.list;

import de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry;
import de.rettichlp.therettingtoncompanion.gui.PatternEditBox;
import de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen;
import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.TextColor;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.OFF;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static de.rettichlp.therettingtoncompanion.utils.ModUtils.isValidPattern;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

public class HiddenMessageEntry extends AbstractEntry {

    private final Minecraft minecraft = Minecraft.getInstance();

    private final HiddenMessage hiddenMessage;

    private final PatternEditBox patternEditBox; // regex input
    private final CycleButton<OnOffCycleButtonEntry> toggleButton; // activate/deactivate
    private final Button deleteButton; // delete

    protected HiddenMessageEntry(HiddenMessage hiddenMessage) {
        this.hiddenMessage = hiddenMessage;

        this.patternEditBox = new PatternEditBox(this.minecraft.font, this.hiddenMessage.getPatternString(), this.hiddenMessage::setPatternString);

        this.toggleButton = CycleButton.builder(OnOffCycleButtonEntry::value, this.hiddenMessage.isActive() ? ON : OFF)
                .withValues(OnOffCycleButtonEntry.values())
                .displayOnlyValue()
                .create(0, 0, 30, 20, empty(), (_, value) -> this.hiddenMessage.setActive(value == ON));

        this.deleteButton = Button.builder(literal("X").withColor(TextColor.RED), _ -> {
            configuration.chat().getHiddenMessages().removeIf(cr -> cr.equals(this.hiddenMessage));
            this.minecraft.gui.setScreen(new TRCOptionsScreen("chat", new PauseScreen(true), true));
        }).build();
        this.deleteButton.setWidth(20);
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.patternEditBox.setWidth(getContentWidth() / 2);
        this.patternEditBox.setPosition(getContentX(), getContentYMiddle() - this.patternEditBox.getHeight() / 2);
        this.patternEditBox.extractRenderState(graphics, mouseX, mouseY, a);

        this.toggleButton.setPosition(this.patternEditBox.getX() + this.patternEditBox.getWidth() + 8, getContentYMiddle() - this.toggleButton.getHeight() / 2);
        this.toggleButton.extractRenderState(graphics, mouseX, mouseY, a);

        this.deleteButton.setPosition(getContentRight() - this.deleteButton.getWidth(), getContentYMiddle() - this.deleteButton.getHeight() / 2);
        this.deleteButton.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return List.of(this.patternEditBox, this.toggleButton, this.deleteButton);
    }

    @Data
    public static class HiddenMessage {

        private final String providerModId;

        private String patternString;
        private boolean active;

        public HiddenMessage(String patternString) {
            this.providerModId = MOD_ID;
            this.patternString = patternString;
            this.active = true;
        }

        public HiddenMessage(String patternString, String providerModId) {
            this.providerModId = providerModId;
            this.patternString = patternString;
            this.active = true;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof HiddenMessage hiddenMessage
                    && this.providerModId.equalsIgnoreCase(hiddenMessage.providerModId)
                    && this.patternString.equalsIgnoreCase(hiddenMessage.patternString)
                    && this.active == hiddenMessage.isActive();
        }

        public Optional<Pattern> getPattern() {
            return isValidPattern(this.patternString)
                    ? Optional.of(compile(this.patternString, CASE_INSENSITIVE))
                    : Optional.empty();
        }
    }
}
