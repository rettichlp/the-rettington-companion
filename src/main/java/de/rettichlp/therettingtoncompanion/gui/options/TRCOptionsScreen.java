package de.rettichlp.therettingtoncompanion.gui.options;

import de.rettichlp.therettingtoncompanion.gui.options.tabs.AbstractTRCOptionsTab;
import de.rettichlp.therettingtoncompanion.gui.options.tabs.ChatOptionsTab;
import de.rettichlp.therettingtoncompanion.gui.options.tabs.GeneralOptionsTab;
import de.rettichlp.therettingtoncompanion.gui.options.tabs.InventoryOptionsTab;
import de.rettichlp.therettingtoncompanion.gui.options.tabs.VisualsOptionsTab;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_NAME;
import static de.rettichlp.therettingtoncompanion.utils.ModUtils.getVersionString;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.client.gui.screens.ConfirmLinkScreen.confirmLink;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.network.chat.CommonComponents.GUI_BACK;
import static net.minecraft.network.chat.CommonComponents.GUI_DONE;
import static net.minecraft.network.chat.CommonComponents.SPACE;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.resources.Identifier.withDefaultNamespace;

public class TRCOptionsScreen extends Screen {

    public static final int SPACING_HORIZONTAL = 8;
    public static final int SPACING_VERTICAL = 4;

    protected final HeaderAndFooterLayout layout;

    private static final URI DISCORD_INVITE = URI.create("https://discord.gg/mZGAAwhPHu");
    private static final int DISCORD_COLOR = 0x5865F2;
    private static final URI MODRINTH = URI.create("https://modrinth.com/mod/ucutils");
    private static final int MODRINTH_COLOR = 0x1BD96B;
    private static final Identifier MENU_LIST_BACKGROUND = withDefaultNamespace("textures/gui/menu_list_background.png");
    private static final Identifier INWORLD_MENU_LIST_BACKGROUND = withDefaultNamespace("textures/gui/inworld_menu_list_background.png");

    private static final List<AbstractTRCOptionsTab> TABS = List.of(
            new GeneralOptionsTab(),
            new VisualsOptionsTab(),
            new ChatOptionsTab(),
            new InventoryOptionsTab()
    );

    private final String selectedTabId;
    private final Screen lastScreen;
    private final boolean renderBackground;

    private TRCOptionsList optionsList;

    public TRCOptionsScreen(String selectedTabId, Screen lastScreen, boolean renderBackground) {
        MutableComponent title = empty()
                .append(literal(MOD_NAME))
                .append(SPACE)
                .append(translatable("options.title"));

        super(title);

        int headerHeight = this.font.lineHeight * 2 + 4 * SPACING_VERTICAL + 20;
        int footerHeight = 2 * SPACING_VERTICAL + 20;

        this.layout = new HeaderAndFooterLayout(this, headerHeight, footerHeight);
        this.selectedTabId = selectedTabId;
        this.lastScreen = lastScreen;
        this.renderBackground = renderBackground;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (this.renderBackground) {
            extractListBackground(graphics);
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);

        if (this.renderBackground) {
            extractListSeparators(graphics);
        }
    }

    @Override
    public void onClose() {
        // TODO safe

        this.minecraft.setScreen(null);
    }

    @Override
    protected void init() {
        // header
        LinearLayout header = this.layout.addToHeader(vertical().spacing(SPACING_VERTICAL));
        header.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);
        header.addChild(new StringWidget(literal("v" + getVersionString()), this.font), LayoutSettings::alignHorizontallyCenter);
        LinearLayout tabs = header.addChild(horizontal().spacing(SPACING_HORIZONTAL));
        TABS.forEach(abstractTRCOptionsTab -> tabs.addChild(abstractTRCOptionsTab.getTabButton(this.lastScreen)));

        // content
        AbstractTRCOptionsTab abstractTRCOptionsTab = TABS.stream()
                .filter(tab -> tab.getId().equals(this.selectedTabId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No options tab found for id: " + this.selectedTabId));
        this.optionsList = abstractTRCOptionsTab.getOptionsList(this);
        this.layout.addToContents(this.optionsList);

        // footer
        LinearLayout footer = this.layout.addToFooter(horizontal().spacing(SPACING_HORIZONTAL));
        footer.addChild(Button.builder(GUI_BACK, _ -> onBack()).width(120).build());
        footer.addChild(Button.builder(GUI_DONE, _ -> onClose()).width(168).build());
        footer.addChild(Button.builder(literal("Discord").withColor(DISCORD_COLOR), confirmLink(this, DISCORD_INVITE)).width(56).build());
        footer.addChild(Button.builder(literal("Modrinth").withColor(MODRINTH_COLOR), confirmLink(this, MODRINTH)).width(56).build());

        this.layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.optionsList != null) {
            this.optionsList.updateSize(this.width, this.layout);
        }
    }

    public void onBack() {
        // TODO safe

        this.minecraft.setScreen(this.lastScreen);
    }

    /**
     * @see net.minecraft.client.gui.components.AbstractSelectionList#extractListSeparators(GuiGraphicsExtractor)
     */
    protected void extractListSeparators(@NonNull GuiGraphicsExtractor graphics) {
        Identifier headerSeparator = this.minecraft.level == null ? HEADER_SEPARATOR : INWORLD_HEADER_SEPARATOR;
        Identifier footerSeparator = this.minecraft.level == null ? FOOTER_SEPARATOR : INWORLD_FOOTER_SEPARATOR;
        graphics.blit(GUI_TEXTURED, headerSeparator, this.layout.getX(), this.layout.getHeaderHeight() - 2, 0.0F, 0.0F, this.layout.getWidth(), 2, 32, 2);
        graphics.blit(GUI_TEXTURED, footerSeparator, this.layout.getX(), this.layout.getHeight() - this.layout.getFooterHeight(), 0.0F, 0.0F, this.layout.getWidth(), 2, 32, 2);
    }

    /**
     * @see net.minecraft.client.gui.components.AbstractSelectionList#extractListBackground(GuiGraphicsExtractor)
     */
    protected void extractListBackground(@NonNull GuiGraphicsExtractor graphics) {
        Identifier menuListBackground = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
        graphics.blit(GUI_TEXTURED, menuListBackground, this.layout.getX(), this.layout.getHeaderHeight(), 0.0F, 0.0F, this.layout.getWidth(), this.layout.getContentHeight(), 32, 32);
    }
}
