package de.rettichlp.therettingtoncompanion.gui.screens;

import de.rettichlp.therettingtoncompanion.configuration.ChatTab;
import de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry;
import de.rettichlp.therettingtoncompanion.gui.PatternEditBox;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.OFF;
import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen.SPACING_HORIZONTAL;
import static de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen.SPACING_VERTICAL;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.FOCUSED_CHAT_TAB;
import static de.rettichlp.therettingtoncompanion.utils.ModUtils.getCurrentServerBaseDomain;
import static net.minecraft.client.gui.layouts.FrameLayout.centerInRectangle;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.network.chat.CommonComponents.GUI_DONE;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.TextColor.RED;
import static net.minecraft.resources.Identifier.withDefaultNamespace;

public class ChatTabPopupScreen extends Screen {

    private static final Identifier BACKGROUND_SPRITE = withDefaultNamespace("popup/background");

    private final LinearLayout layout = vertical().spacing(SPACING_VERTICAL);

    private final @Nullable Screen backgroundScreen;
    private final ChatTab chatTab;

    public ChatTabPopupScreen(@Nullable Screen backgroundScreen, @NonNull ChatTab chatTab) {
        super(literal("chat_tab"));
        this.backgroundScreen = backgroundScreen;
        this.chatTab = chatTab;
    }

    @Override
    public void onClose() {
        configuration.saveToFile();

        if (this.backgroundScreen != null) {
            // force the chat screen to re-init so its tab button row picks up the (possibly changed) tab layout
            this.backgroundScreen.resize(this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
        }
        this.minecraft.gui.setScreen(this.backgroundScreen);
    }

    @Override
    protected void init() {
        if (this.backgroundScreen != null) {
            this.backgroundScreen.init(this.width, this.height);
        }

        this.layout.newCellSettings().alignHorizontallyCenter();

        EditBox nameInput = this.layout.addChild(new EditBox(this.font, 200, 20, empty()), LayoutSettings::alignHorizontallyCenter);
        nameInput.setValue(this.chatTab.getName());
        nameInput.setMaxLength(32);
        nameInput.setHint(translatable("trc.chat_screen.chat_tabs.name_hint"));
        nameInput.setResponder(this.chatTab::setName);

        LinearLayout serverBoundRow = this.layout.addChild(horizontal().spacing(SPACING_HORIZONTAL));
        serverBoundRow.addChild(new StringWidget(200 - 60 - 8, 9, translatable("trc.chat_screen.chat_tabs.server_bound"), this.font), LayoutSettings::alignVerticallyMiddle);
        serverBoundRow.addChild(CycleButton.builder(OnOffCycleButtonEntry::value, this.chatTab.getServerBoundDomain() != null ? ON : OFF)
                .withValues(OnOffCycleButtonEntry.values())
                .displayOnlyValue()
                .create(0, 0, 60, 20, empty(), (_, value) -> this.chatTab.setServerBoundDomain(value == ON ? getCurrentServerBaseDomain() : null)));

        List<String> patternStrings = this.chatTab.getPatternStrings();
        for (int i = 0; i < patternStrings.size(); i++) {
            int index = i;
            LinearLayout row = this.layout.addChild(horizontal().spacing(SPACING_HORIZONTAL));

            PatternEditBox patternEditBox = new PatternEditBox(this.font, patternStrings.get(index), value -> patternStrings.set(index, value));
            patternEditBox.setWidth(172);
            row.addChild(patternEditBox);

            row.addChild(Button.builder(literal("X").withColor(RED), _ -> {
                patternStrings.remove(index);
                reload();
            }).width(20).build());
        }

        this.layout.addChild(Button.builder(literal("+"), _ -> {
            patternStrings.add("");
            reload();
        }).width(200).build(), LayoutSettings::alignHorizontallyCenter);

        LinearLayout buttonRow = horizontal().spacing(SPACING_HORIZONTAL);
        buttonRow.addChild(Button.builder(translatable("trc.chat_screen.chat_tabs.delete_tab").withColor(RED), _ -> {
            configuration.chat().getChatTabs().remove(this.chatTab);
            if (FOCUSED_CHAT_TAB == this.chatTab) {
                FOCUSED_CHAT_TAB = null;
            }
            onClose();
        }).width(96).build());
        buttonRow.addChild(Button.builder(GUI_DONE, _ -> onClose()).width(96).build());
        this.layout.addChild(buttonRow);

        this.layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    public void added() {
        super.added();
        if (this.backgroundScreen != null) {
            this.backgroundScreen.clearFocus();
        }
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (this.backgroundScreen != null) {
            this.backgroundScreen.extractBackground(graphics, mouseX, mouseY, a);
            graphics.nextStratum();
            this.backgroundScreen.extractRenderState(graphics, -1, -1, a);
            graphics.nextStratum();
            extractTransparentBackground(graphics);
        } else {
            super.extractBackground(graphics, mouseX, mouseY, a);
        }
        graphics.blitSprite(GUI_TEXTURED, BACKGROUND_SPRITE, this.layout.getX() - 18, this.layout.getY() - 18, this.layout.getWidth() + 36, this.layout.getHeight() + 36);
    }

    @Override
    protected void repositionElements() {
        if (this.backgroundScreen != null) {
            this.backgroundScreen.resize(this.width, this.height);
        }
        this.layout.arrangeElements();
        centerInRectangle(this.layout, this.getRectangle());
    }

    private void reload() {
        this.minecraft.gui.setScreen(new ChatTabPopupScreen(this.backgroundScreen, this.chatTab));
    }
}
