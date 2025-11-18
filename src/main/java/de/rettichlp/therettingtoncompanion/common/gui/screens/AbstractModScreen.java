package de.rettichlp.therettingtoncompanion.common.gui.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.nonNull;
import static net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.vertical;

public abstract class AbstractModScreen extends Screen {

    protected static final int MARGIN = 8;

    protected final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

    private static final Identifier MENU_LIST_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/menu_list_background.png");
    private static final Identifier INWORLD_MENU_LIST_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_menu_list_background.png");

    private final Text subTitle;
    private final Screen parent;
    private final boolean renderBackground;

    public AbstractModScreen(Text title, Text subTitle, Screen parent, boolean renderBackground) {
        super(title);
        this.subTitle = subTitle;
        this.parent = parent;
        this.renderBackground = renderBackground;
    }

    public abstract void initBody();

    public abstract void doOnClose();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.renderBackground) {
            drawMenuListBackground(context);
        }

        super.render(context, mouseX, mouseY, delta);

        if (this.renderBackground) {
            drawHeaderAndFooterSeparators(context);
        }
    }

    @Override
    public void close() {
        this.client.setScreen(null);
        doOnClose();
    }

    @Override
    protected void init() {
        initHeader();
        initBody();
        initFooter();
        this.layout.forEachChild(this::addDrawableChild);
        refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    public void back() {
        if (nonNull(this.parent) && nonNull(this.client) && nonNull(this.client.player)) {
            this.client.setScreen(this.parent);
            doOnClose();
        } else {
            close();
        }
    }

    protected void initHeader() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addHeader(vertical().spacing(4));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add(new TextWidget(this.title, this.textRenderer));
        directionalLayoutWidget.add(new TextWidget(this.subTitle, this.textRenderer));
    }

    protected void initFooter() {}

    /**
     * @see EntryListWidget#drawHeaderAndFooterSeparators(DrawContext)
     */
    private void drawHeaderAndFooterSeparators(@NotNull DrawContext context) {
        Identifier identifier = this.client.world == null ? Screen.HEADER_SEPARATOR_TEXTURE : Screen.INWORLD_HEADER_SEPARATOR_TEXTURE;
        Identifier identifier2 = this.client.world == null ? Screen.FOOTER_SEPARATOR_TEXTURE : Screen.INWORLD_FOOTER_SEPARATOR_TEXTURE;
        context.drawTexture(GUI_TEXTURED, identifier, this.layout.getX(), this.layout.getHeaderHeight(), 0.0F, 0.0F, this.layout.getWidth(), 2, 32, 2);
        context.drawTexture(GUI_TEXTURED, identifier2, this.layout.getX(), this.layout.getHeight() - this.layout.getFooterHeight(), 0.0F, 0.0F, this.layout.getWidth(), 2, 32, 2);
    }

    /**
     * @see EntryListWidget#drawMenuListBackground(DrawContext)
     */
    private void drawMenuListBackground(@NotNull DrawContext context) {
        Identifier identifier = this.client.world == null ? MENU_LIST_BACKGROUND_TEXTURE : INWORLD_MENU_LIST_BACKGROUND_TEXTURE;
        context.drawTexture(GUI_TEXTURED, identifier, this.layout.getX(), this.layout.getHeaderHeight(), 0.0F, 0.0F, this.layout.getWidth(), this.layout.getContentHeight(), 32, 32);
    }
}
