package de.rettichlp.therettingtoncompanion.common.gui.screens.options;

import de.rettichlp.therettingtoncompanion.common.gui.screens.AbstractModScreen;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.tab.TabMenuContentWidget;
import de.rettichlp.therettingtoncompanion.common.gui.screens.components.tab.TabMenuListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_NAME;
import static net.minecraft.client.gui.screen.ConfirmLinkScreen.opening;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.vertical;
import static net.minecraft.screen.ScreenTexts.BACK;
import static net.minecraft.screen.ScreenTexts.DONE;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;
import static net.minecraft.text.Text.translatable;

@Environment(EnvType.CLIENT)
public class ModOptionScreen extends AbstractModScreen {

    private static final URI DISCORD_INVITE = URI.create("https://discord.gg/mZGAAwhPHu");
    private static final int DISCORD_COLOR = 0x5865F2;
    private static final URI MODRINTH = URI.create("https://modrinth.com/mod/pkutils");
    private static final int MODRINTH_COLOR = 0x1BD96B;

    @Nullable
    private TabMenuListWidget tabMenuListWidget;
    @Nullable
    private TabMenuContentWidget tabMenuContentWidget;

    private final String activeTabId;

    public ModOptionScreen() {
        super(empty()
                .append(MOD_NAME).append(" ")
                .append(translatable("options.title")), new GameMenuScreen(true));
        this.activeTabId = "activeTabId";
    }

    public ModOptionScreen(String activeTabId) {
        super(empty()
                .append(MOD_NAME).append(" ")
                .append(translatable("options.title")), new GameMenuScreen(true));
        this.activeTabId = activeTabId;
    }

    public ModOptionScreen(Text subTitel, String activeTabId, boolean renderBackground) {
        super(empty()
                .append(MOD_NAME).append(" ")
                .append(translatable("options.title")), subTitel, new GameMenuScreen(true), renderBackground);
        this.activeTabId = activeTabId;
    }

    @Override
    public void initBody() {
        this.tabMenuListWidget = this.layout.addBody(new TabMenuListWidget(this.width / 3, this.layout.getContentHeight() - 2, 0, this.layout.getHeaderHeight() + 2), Positioner::alignLeft);  // 2 for separator
        this.tabMenuContentWidget = this.layout.addBody(new TabMenuContentWidget(this.width / 3 * 2, this.layout.getContentHeight() - 2, this.width / 3, this.layout.getHeaderHeight() + 2), Positioner::alignRight);  // 2 for separator





//        Window window = MinecraftClient.getInstance().getWindow();
//        System.out.println("H: " + window.getScaledWidth());
//        System.out.println("W: " + window.getScaledHeight());
//
//        int width = this.layout.getWidth();
//        int height = this.layout.getHeight();
//
//        System.out.println("Layout W: " + width);
//        System.out.println("Layout H: " + height);
//        System.out.println("Header H: " + this.layout.getHeaderHeight());
//
//        DirectionalLayoutWidget directionalLayoutWidget = vertical();
//        for (int i = 0; i < 30; i++) {
//            directionalLayoutWidget.add(new TextWidget(Text.literal("Entry " + i), MinecraftClient.getInstance().textRenderer));
//        }
//        this.scroll = this.layout.addBody(new ScrollableLayoutWidget(MinecraftClient.getInstance(), directionalLayoutWidget, 200));
//
//        AxisGridWidget axisGridWidget = this.layout.addBody(new AxisGridWidget(0, this.layout.getHeaderHeight(), width, height, HORIZONTAL));
//
//        AxisGridWidget tabsAxisGridWidget = axisGridWidget.add(new AxisGridWidget(width / 3, height, VERTICAL));
//        System.out.println("Tabs width: " + tabsAxisGridWidget.getWidth());
//        AxisGridWidget contentAxisGridWidget = axisGridWidget.add(new AxisGridWidget(width - width / 3, height, VERTICAL));
//
//        System.out.println("All width: " + (tabsAxisGridWidget.getWidth() + contentAxisGridWidget.getWidth()) + " window: " + MinecraftClient.getInstance().getWindow().getScaledWidth());
//
//
//        this.modOptionTabs.forEach(abstractOptionTab -> {
//            ButtonWidget tabButtonWidget = abstractOptionTab.getTabButtonWidget();
//            System.out.println("Button width: " + tabButtonWidget.getWidth());
//            tabsAxisGridWidget.add(tabButtonWidget);
//        });
//
//        List<AbstractOptionTab> activeModOptionTabs = this.modOptionTabs.stream()
//                .filter(abstractOptionTab -> abstractOptionTab.getTabId().equals(this.activeTabId))
//                .toList();
//
//        if (activeModOptionTabs.size() != 1) {
//            throw new IllegalStateException("There must be exactly one active mod option tab! Found: " + activeModOptionTabs.size());
//        } else {
//            contentAxisGridWidget.add(activeModOptionTabs.getFirst().getTabContentWidget());
//        }
//
//        tabsAxisGridWidget.refreshPositions();
//        contentAxisGridWidget.refreshPositions();
//        axisGridWidget.refreshPositions();
    }

//    @Override
//    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        super.render(context, mouseX, mouseY, delta);
//        context.drawHorizontalLine(this.layout.getX(), this.layout.getWidth(), this.layout.getY(), Color.RED.getRGB());
//        context.drawHorizontalLine(this.layout.getX(), this.layout.getWidth(), this.layout.getHeaderHeight(), Color.ORANGE.getRGB());
//        context.drawVerticalLine(this.layout.getWidth() / 3, 0, this.layout.getHeight(), Color.BLUE.getRGB());
//        context.drawVerticalLine(this.layout.getWidth() / 3 * 2, 0, this.layout.getHeight(), Color.BLUE.getRGB());
//
//        if (this.scroll != null) {
//            context.drawHorizontalLine(this.scroll.getX(), this.scroll.getX() + this.scroll.getWidth(), this.scroll.getY(), Color.GREEN.getRGB());
//            context.drawHorizontalLine(this.scroll.getX(), this.scroll.getX() + this.scroll.getWidth(), this.scroll.getY() + this.scroll.getHeight(), Color.PINK.getRGB());
//
//            context.drawVerticalLine(this.scroll.getX(), this.scroll.getY(), this.scroll.getY() + this.scroll.getHeight(), Color.CYAN.getRGB());
//            context.drawVerticalLine(this.scroll.getX() + this.scroll.getWidth(),  this.scroll.getY(), this.scroll.getY() + this.scroll.getHeight(), Color.MAGENTA.getRGB());
//
//        }
//
//    }

    @Override
    public void doOnClose() {
        // FIXME configuration.saveToFile();
    }

    @Override
    protected void initFooter() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(horizontal().spacing(8));
        directionalLayoutWidget.add(ButtonWidget.builder(BACK, button -> back()).width(120).build());
        directionalLayoutWidget.add(ButtonWidget.builder(DONE, button -> close()).width(200).build());
        directionalLayoutWidget.add(ButtonWidget.builder(of("Discord").copy().withColor(DISCORD_COLOR), opening(this, DISCORD_INVITE)).width(56).build());
        directionalLayoutWidget.add(ButtonWidget.builder(of("Modrinth").copy().withColor(MODRINTH_COLOR), opening(this, MODRINTH)).width(56).build());
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.tabMenuListWidget != null) {
            this.tabMenuListWidget.position(this.width / 3, this.layout.getContentHeight() - 2, this.tabMenuListWidget.getX(), this.layout.getHeaderHeight() + 2); // 2 for separator
        }

        if (this.tabMenuContentWidget != null) {
            this.tabMenuContentWidget.position(this.width / 3 * 2, this.layout.getContentHeight() - 2, this.tabMenuContentWidget.getX(), this.layout.getHeaderHeight() + 2); // 2 for separator
        }
    }
}
