package de.rettichlp.therettingtoncompanion.common.gui.screens.options;

import de.rettichlp.therettingtoncompanion.common.gui.screens.AbstractModScreen;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.scrollable.ScrollableListEntry;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs.AbstractOptionTab;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs.ChatOptionTab;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs.GeneralOptionTab;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.tabs.OverlayOptionTab;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;

import java.net.URI;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_NAME;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.common.utils.ModUtils.getVersionString;
import static net.minecraft.client.gui.screen.ConfirmLinkScreen.opening;
import static net.minecraft.client.gui.widget.AxisGridWidget.DisplayAxis.VERTICAL;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.screen.ScreenTexts.BACK;
import static net.minecraft.screen.ScreenTexts.DONE;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;
import static net.minecraft.text.Text.translatable;

public class ModOptionScreen extends AbstractModScreen {

    private static final Text TITLE = empty()
            .append(MOD_NAME).append(" ")
            .append(translatable("options.title"));
    private static final Text SUB_TITLE = of("v" + getVersionString());
    private static final URI DISCORD_INVITE = URI.create("https://discord.gg/mZGAAwhPHu");
    private static final int DISCORD_COLOR = 0x5865F2;
    private static final URI MODRINTH = URI.create("https://modrinth.com/mod/pkutils");
    private static final int MODRINTH_COLOR = 0x1BD96B;

    private static final List<AbstractOptionTab> OPTION_TABS = List.of(
            new GeneralOptionTab(),
            new OverlayOptionTab(),
            new ChatOptionTab()
    );

    private final String activeOptionTabId;

    private DirectionalLayoutWidget tabWidget;
    private ElementListWidget<ScrollableListEntry> contentWidget;

    public ModOptionScreen() {
        super(TITLE, SUB_TITLE, new GameMenuScreen(true), false);
        this.activeOptionTabId = "general";
    }

    public ModOptionScreen(String activeOptionTabId) {
        super(TITLE, SUB_TITLE, new GameMenuScreen(true), false);
        this.activeOptionTabId = activeOptionTabId;
    }

    @Override
    public void initBody() {
        AbstractOptionTab abstractOptionTab = OPTION_TABS.stream()
                .filter(ot -> ot.getId().equals(this.activeOptionTabId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No OptionTab found for id: " + this.activeOptionTabId));

        AxisGridWidget axisGridWidget = this.layout.addBody(new AxisGridWidget(0, this.layout.getHeaderHeight(), this.layout.getWidth(), this.layout.getContentHeight(), VERTICAL));

        this.tabWidget = axisGridWidget.add(horizontal().spacing(MARGIN));
        OPTION_TABS.forEach(ot -> this.tabWidget.add(ot.getButton(ot.equals(abstractOptionTab))));

        this.contentWidget = axisGridWidget.add(abstractOptionTab.createContentWidget(this.layout.getWidth(), this.layout.getContentHeight() - this.tabWidget.getHeight() - 4, 0, this.layout.getHeaderHeight() + this.tabWidget.getHeight() + 4));

        axisGridWidget.forEachChild(this::addDrawableChild);
    }

    @Override
    public void doOnClose() {
        configuration.saveToFile();
    }

    @Override
    protected void refreshWidgetPositions() {
        super.refreshWidgetPositions();

        if (this.tabWidget != null) {
            this.tabWidget.setPosition(MARGIN, this.layout.getHeaderHeight());
        }

        if (this.contentWidget != null) {
            this.contentWidget.position(this.layout.getWidth(), this.layout.getContentHeight() - this.tabWidget.getHeight() - 4, 0, this.layout.getHeaderHeight() + this.tabWidget.getHeight() + 4);
        }
    }

    @Override
    protected void initFooter() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(horizontal().spacing(8));
        directionalLayoutWidget.add(ButtonWidget.builder(BACK, button -> back()).width(120).build());
        directionalLayoutWidget.add(ButtonWidget.builder(DONE, button -> close()).width(200).build());
        directionalLayoutWidget.add(ButtonWidget.builder(of("Discord").copy().withColor(DISCORD_COLOR), opening(this, DISCORD_INVITE)).width(56).build());
        directionalLayoutWidget.add(ButtonWidget.builder(of("Modrinth").copy().withColor(MODRINTH_COLOR), opening(this, MODRINTH)).width(56).build());
    }
}
