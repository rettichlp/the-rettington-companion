package de.rettichlp.therettingtoncompanion.gui.options.list;

import de.rettichlp.therettingtoncompanion.gui.ColorButton;
import de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry;
import de.rettichlp.therettingtoncompanion.gui.screens.ColorSelectionPopupScreen;
import de.rettichlp.therettingtoncompanion.gui.screens.SoundSelectionPopupScreen;
import de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen;
import de.rettichlp.therettingtoncompanion.models.ChatRegex;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.regex.Pattern;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.OFF;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static de.rettichlp.therettingtoncompanion.models.ChatRegex.isValidPattern;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

public class ChatRegexEntry extends AbstractEntry {

    private static final String PRIO_LITERAL = "Prio ";

    private final Minecraft minecraft = Minecraft.getInstance();

    private final ChatRegex chatRegex;
    private final boolean editable;

    private final EditBox editBox; // regex input
    private final CycleButton<OnOffCycleButtonEntry> toggleButton; // activate/deactivate
    private final ColorButton colorButton; // colour select (not final and null for dynamic color update)
    private final Button soundSelectionButton;
    private final StringWidget stringWidgetPriority; // priority display
    private final Button priorityIncreaseButton; // increase priority
    private final Button priorityDecreaseButton; // decrease priority
    private final Button deleteButton; // delete

    protected ChatRegexEntry(@NonNull ChatRegex chatRegex, boolean editable) {
        ChatRegex defaultChatRegex = configuration.chat().regex().getDefaulChatRegex();

        this.chatRegex = chatRegex;
        this.editable = editable;

        this.editBox = new EditBox(this.minecraft.font, 0, 20, empty());
        this.editBox.setValue(this.editable ? this.chatRegex.getPattern().map(Pattern::pattern).orElse("null") : this.minecraft.getGameProfile().name());
        this.editBox.setHint(literal(this.editBox.getValue()));
        this.editBox.setEditable(this.editable);
        this.editBox.setResponder(value -> {
            if (isValidPattern(value)) {
                this.editBox.setTextColor(WHITE.getRGB());
                this.chatRegex.setPatternString(value);
            } else {
                this.editBox.setTextColor(RED.getRGB());
            }
        });

        boolean enabled = this.editable ? this.chatRegex.isActive() : defaultChatRegex.isActive();
        this.toggleButton = CycleButton.builder(OnOffCycleButtonEntry::value, enabled ? ON : OFF)
                .withValues(OnOffCycleButtonEntry.values())
                .displayOnlyValue()
                .create(0, 0, 30, 20, empty(), (_, value) -> (this.editable ? this.chatRegex : defaultChatRegex).setActive(value == ON));

        this.colorButton = new ColorButton(0, 0, 30, 20, this.chatRegex.getColor(), button -> this.minecraft.setScreen(new ColorSelectionPopupScreen(this.minecraft.screen, this.chatRegex.getColor(), color -> {
            (this.editable ? this.chatRegex : defaultChatRegex).setColor(color);
            ((ColorButton) button).setColor(color);
        })));

        this.soundSelectionButton = Button.builder(literal("🔊"), _ -> this.minecraft.setScreen(new SoundSelectionPopupScreen(this.minecraft.screen, this.chatRegex.getSoundIdentifier(), (this.editable ? this.chatRegex : defaultChatRegex)::setSoundIdentifierString)))
                .width(30)
                .build();

        this.stringWidgetPriority = new StringWidget(literal(PRIO_LITERAL + this.chatRegex.getPriority()), this.minecraft.font);
        this.stringWidgetPriority.setWidth(this.minecraft.font.width(this.stringWidgetPriority.getMessage()));

        this.priorityIncreaseButton = Button.builder(literal("+"), _ -> {
            this.chatRegex.setPriority(min(9, this.chatRegex.getPriority() + 1));
            this.stringWidgetPriority.setMessage(literal(PRIO_LITERAL + this.chatRegex.getPriority()));
        }).build();
        this.priorityIncreaseButton.setWidth(20);
        this.priorityIncreaseButton.setHeight(10);
        this.priorityIncreaseButton.active = this.editable;

        this.priorityDecreaseButton = Button.builder(literal("-"), _ -> {
            this.chatRegex.setPriority(max(0, this.chatRegex.getPriority() - 1));
            this.stringWidgetPriority.setMessage(literal(PRIO_LITERAL + this.chatRegex.getPriority()));
        }).build();
        this.priorityDecreaseButton.setWidth(20);
        this.priorityDecreaseButton.setHeight(10);
        this.priorityDecreaseButton.active = this.editable;

        this.deleteButton = Button.builder(literal("X").withStyle(ChatFormatting.RED), _ -> {
            configuration.chat().regex().getChatRegexes().removeIf(cr -> cr.equals(this.chatRegex));
            this.minecraft.setScreen(new TRCOptionsScreen("chat", new PauseScreen(true), true));
        }).build();
        this.deleteButton.setWidth(20);
        this.deleteButton.active = this.editable;
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        this.editBox.setWidth(getContentWidth() / 2);
        this.editBox.setPosition(getContentX(), getContentYMiddle() - this.editBox.getHeight() / 2);
        this.editBox.extractRenderState(graphics, mouseX, mouseY, a);

        this.toggleButton.setPosition(this.editBox.getX() + this.editBox.getWidth() + 8, getContentYMiddle() - this.toggleButton.getHeight() / 2);
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
        return List.of(this.editBox, this.toggleButton, this.colorButton, this.soundSelectionButton, this.stringWidgetPriority, this.priorityIncreaseButton, this.priorityDecreaseButton, this.deleteButton);
    }
}
