package de.rettichlp.therettingtoncompanion.common.gui.screens.popup;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.vertical;
import static net.minecraft.text.Text.empty;
import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Identifier.ofVanilla;

public abstract class AbstractReturningPopupScreen<T> extends Screen {

    protected final DirectionalLayoutWidget layout = vertical();
    protected final DirectionalLayoutWidget buttonLayout = horizontal().spacing(8);
    protected final Consumer<T> onClose;

    private static final Identifier BACKGROUND_TEXTURE = ofVanilla("popup/background");

    private final Screen parent;

    public AbstractReturningPopupScreen(Screen parent, Consumer<T> onClose) {
        super(empty());
        this.parent = parent;
        this.onClose = onClose;
    }

    public abstract void initBody();

    public abstract void initButtons();

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.parent.init(this.width, this.height);
        this.layout.spacing(12).getMainPositioner().alignHorizontalCenter();

        TextWidget titleTextWidget = new TextWidget(this.title.copy().formatted(BOLD), this.textRenderer);
        this.layout.add(titleTextWidget);

        initBody();
        initButtons();

        this.layout.add(this.buttonLayout);
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    @Override
    public void onDisplayed() {
        super.onDisplayed();
        this.parent.blur();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.parent.renderBackground(context, mouseX, mouseY, deltaTicks);
        context.createNewRootLayer();
        this.parent.render(context, -1, -1, deltaTicks);
        context.createNewRootLayer();
        this.renderInGameBackground(context);
        context.drawGuiTexture(GUI_TEXTURED, BACKGROUND_TEXTURE, this.layout.getX() - 18, this.layout.getY() - 18, this.layout.getWidth() + 36, this.layout.getHeight() + 36);
    }

    @Override
    protected void refreshWidgetPositions() {
        this.parent.resize(this.width, this.height);
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
    }

    protected void onReturn(T t) {
        this.onClose.accept(t);
        close();
    }
}
