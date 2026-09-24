package de.rettichlp.therettingtoncompanion.gui.screens;

import de.rettichlp.therettingtoncompanion.gui.ColorButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.util.function.IntSupplier;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.visualsService;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.CUSTOM_CROSSHAIR_SIZE;
import static de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen.SPACING_HORIZONTAL;
import static de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen.SPACING_VERTICAL;
import static de.rettichlp.therettingtoncompanion.services.VisualsService.VANILLA_TEXT_COLORS;
import static java.util.Arrays.fill;
import static net.minecraft.client.gui.layouts.FrameLayout.centerInRectangle;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.network.chat.CommonComponents.GUI_CANCEL;
import static net.minecraft.network.chat.CommonComponents.GUI_DONE;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.translatable;

public class CrosshairEditorScreen extends Screen {

    private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("popup/background");
    private static final int PIXEL_SIZE = 16;
    private static final int PALETTE_COLUMNS = 4;

    private final LinearLayout layout = vertical().spacing(SPACING_VERTICAL);
    private final Screen backgroundScreen;
    private final int[] pixels = configuration.visuals().getCustomCrosshairPixels().clone();

    private Color selectedColor = Color.WHITE;
    private boolean eraserActive = false;
    private Button eraserButton;

    public CrosshairEditorScreen(Screen backgroundScreen) {
        super(translatable("trc.option.visuals.crosshair.edit.label"));
        this.backgroundScreen = backgroundScreen;
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.backgroundScreen);
    }

    @Override
    protected void init() {
        this.layout.newCellSettings().alignHorizontallyCenter();
        this.layout.addChild(new StringWidget(this.title, this.font));

        LinearLayout contentRow = this.layout.addChild(horizontal().spacing(SPACING_HORIZONTAL));
        CrosshairCanvas crosshairCanvas = contentRow.addChild(new CrosshairCanvas(0, 0, CUSTOM_CROSSHAIR_SIZE, PIXEL_SIZE, this.pixels, () -> this.eraserActive ? 0 : this.selectedColor.getRGB()));

        LinearLayout palette = contentRow.addChild(vertical().spacing(SPACING_VERTICAL));

        int currentLength = 0;
        LinearLayout currentRow = horizontal().spacing(SPACING_HORIZONTAL);
        for (TextColor textColor : VANILLA_TEXT_COLORS) {
            Color color = new Color(textColor.getValue());

            currentRow.addChild(new ColorButton(0, 0, 20, 20, color, button -> {
                this.eraserActive = false;
                this.selectedColor = color;
                updatePaletteFocus();
            }));

            if (++currentLength == PALETTE_COLUMNS) {
                palette.addChild(currentRow);
                currentRow = horizontal().spacing(SPACING_HORIZONTAL);
                currentLength = 0;
            }
        }

        if (currentLength > 0) {
            palette.addChild(currentRow);
        }

        int buttonWidth = 20 * PALETTE_COLUMNS + SPACING_HORIZONTAL * (PALETTE_COLUMNS - 1);

        palette.addChild(Button.builder(translatable("trc.crosshair_editor.custom_color"), _ -> this.minecraft.gui.setScreen(new ColorSelectionPopupScreen(this, this.selectedColor, color -> {
            this.eraserActive = false;
            this.selectedColor = color;
            updatePaletteFocus();
        }))).width(buttonWidth).build());

        this.eraserButton = palette.addChild(Button.builder(translatable("trc.crosshair_editor.eraser"), _ -> {
            this.eraserActive = !this.eraserActive;
            updatePaletteFocus();
        }).width(buttonWidth).build());

        palette.addChild(Button.builder(translatable("trc.crosshair_editor.clear"), _ -> fill(this.pixels, 0)).width(buttonWidth).build());
        palette.addChild(Button.builder(GUI_CANCEL, _ -> onClose()).width(buttonWidth).build());
        palette.addChild(Button.builder(GUI_DONE, _ -> onDone()).width(buttonWidth).build());

        updatePaletteFocus();

        this.layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    public void added() {
        super.added();
        this.backgroundScreen.clearFocus();
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.backgroundScreen.extractBackground(graphics, mouseX, mouseY, a);
        graphics.nextStratum();
        this.backgroundScreen.extractRenderState(graphics, -1, -1, a);
        graphics.nextStratum();
        extractTransparentBackground(graphics);
        graphics.blitSprite(GUI_TEXTURED, BACKGROUND_SPRITE, this.layout.getX() - 18, this.layout.getY() - 18, this.layout.getWidth() + 36, this.layout.getHeight() + 36);
    }

    @Override
    protected void repositionElements() {
        this.backgroundScreen.resize(this.width, this.height);
        this.layout.arrangeElements();
        centerInRectangle(this.layout, this.getRectangle());
    }

    private void onDone() {
        configuration.visuals().setCustomCrosshairPixels(this.pixels);
        visualsService.refreshCustomCrosshairTexture();
        onClose();
    }

    private void updatePaletteFocus() {
        this.layout.visitWidgets(abstractWidget -> {
            if (abstractWidget instanceof ColorButton colorButton) {
                colorButton.setFocused(!this.eraserActive && this.selectedColor.equals(colorButton.getColor()));
            }
        });

        if (this.eraserButton != null) {
            this.eraserButton.setFocused(this.eraserActive);
        }
    }

    public static class CrosshairCanvas extends AbstractWidget {

        private static final int CHECKER_COLOR_LIGHT = 0xFF6B6B6B;
        private static final int CHECKER_COLOR_DARK = 0xFF4B4B4B;
        private static final int BORDER_COLOR = 0xFFFFFFFF;

        private final int gridSize;
        private final int pixelSize;
        private final int[] pixels;
        private final IntSupplier selectedArgbSupplier;

        public CrosshairCanvas(int x, int y, int gridSize, int pixelSize, int[] pixels, IntSupplier selectedArgbSupplier) {
            super(x, y, gridSize * pixelSize, gridSize * pixelSize, empty());
            this.gridSize = gridSize;
            this.pixelSize = pixelSize;
            this.pixels = pixels;
            this.selectedArgbSupplier = selectedArgbSupplier;
        }

        @Override
        protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            for (int row = 0; row < this.gridSize; row++) {
                for (int column = 0; column < this.gridSize; column++) {
                    int pixelX = getX() + column * this.pixelSize;
                    int pixelY = getY() + row * this.pixelSize;

                    // checkerboard background so transparent (unpainted) pixels stay visible while editing
                    graphics.fill(pixelX, pixelY, pixelX + this.pixelSize, pixelY + this.pixelSize, (row + column) % 2 == 0 ? CHECKER_COLOR_LIGHT : CHECKER_COLOR_DARK);

                    int argb = this.pixels[row * this.gridSize + column];
                    if ((argb >>> 24) != 0) {
                        graphics.fill(pixelX, pixelY, pixelX + this.pixelSize, pixelY + this.pixelSize, argb);
                    }
                }
            }

            graphics.outline(getX(), getY(), getWidth(), getHeight(), BORDER_COLOR);
        }

        @Override
        public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
            paint(event.x(), event.y());
        }

        @Override
        protected void onDrag(@NonNull MouseButtonEvent event, double dx, double dy) {
            paint(event.x(), event.y());
        }

        @Override
        protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
            // visual editing surface, nothing to narrate
        }

        private void paint(double mouseX, double mouseY) {
            int column = (int) ((mouseX - getX()) / this.pixelSize);
            int row = (int) ((mouseY - getY()) / this.pixelSize);

            if (column < 0 || column >= this.gridSize || row < 0 || row >= this.gridSize) {
                return;
            }

            this.pixels[row * this.gridSize + column] = this.selectedArgbSupplier.getAsInt();
        }
    }
}
