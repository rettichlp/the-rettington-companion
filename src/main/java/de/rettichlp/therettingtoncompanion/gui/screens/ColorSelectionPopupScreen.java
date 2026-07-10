package de.rettichlp.therettingtoncompanion.gui.screens;

import de.rettichlp.therettingtoncompanion.gui.ColorButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.Color;
import java.util.function.Consumer;

import static de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen.SPACING_HORIZONTAL;
import static de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen.SPACING_VERTICAL;
import static java.lang.Integer.parseInt;
import static java.lang.Math.clamp;
import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;
import static net.minecraft.client.gui.layouts.FrameLayout.centerInRectangle;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.network.chat.CommonComponents.GUI_CANCEL;
import static net.minecraft.network.chat.CommonComponents.GUI_DONE;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

public class ColorSelectionPopupScreen extends Screen {

    private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("popup/background");

    private final LinearLayout layout = vertical().spacing(SPACING_VERTICAL);

    private final @Nullable Screen backgroundScreen;
    private final Color initialColor;
    private final Consumer<Color> onClose;

    private EditBox colorInputRed;
    private EditBox colorInputGreen;
    private EditBox colorInputBlue;

    public ColorSelectionPopupScreen(@Nullable Screen backgroundScreen, Color initialColor, Consumer<Color> onClose) {
        super(literal("color"));
        this.backgroundScreen = backgroundScreen;
        this.initialColor = initialColor;
        this.onClose = onClose;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.backgroundScreen);
    }

    @Override
    protected void init() {
        if (this.backgroundScreen != null) {
            this.backgroundScreen.init(this.width, this.height);
        }

        this.layout.newCellSettings().alignHorizontallyCenter();

        // Minecraft colors
        int currentLength = 0;
        LinearLayout currentRow = horizontal().spacing(SPACING_HORIZONTAL);
        for (ChatFormatting chatFormatting : ChatFormatting.values()) {
            if (!chatFormatting.isColor()) {
                continue;
            }

            Color color = new Color(requireNonNull(chatFormatting.getColor()));

            ColorButton colorButton = currentRow.addChild(new ColorButton(0, 0, 30, 20, color, button -> {
                button.setFocused(!button.isFocused());
                onColorChange(color);
            }));
            colorButton.setFocused(this.initialColor.equals(color));

            if (++currentLength == 8) {
                this.layout.addChild(currentRow);
                currentRow = horizontal().spacing(SPACING_HORIZONTAL);
                currentLength = 0;
            }
        }

        this.layout.addChild(currentRow);

        // RGB colors
        LinearLayout rgbRow = this.layout.addChild(horizontal().spacing(SPACING_HORIZONTAL));
        rgbRow.addChild(new StringWidget(literal("R"), this.font), LayoutSettings::alignVerticallyMiddle);
        this.colorInputRed = rgbRow.addChild(new EditBox(this.font, 40, 20, empty()));
        this.colorInputRed.setValue(valueOf(this.initialColor.getRed()));
        this.colorInputRed.setResponder(_ -> onColorChange(getColorFromRGBInputs()));
        rgbRow.addChild(new StringWidget(literal("G"), this.font), LayoutSettings::alignVerticallyMiddle);
        this.colorInputGreen = rgbRow.addChild(new EditBox(this.font, 40, 20, empty()));
        this.colorInputGreen.setValue(valueOf(this.initialColor.getGreen()));
        this.colorInputGreen.setResponder(_ -> onColorChange(getColorFromRGBInputs()));
        rgbRow.addChild(new StringWidget(literal("B"), this.font), LayoutSettings::alignVerticallyMiddle);
        this.colorInputBlue = rgbRow.addChild(new EditBox(this.font, 40, 20, empty()));
        this.colorInputBlue.setValue(valueOf(this.initialColor.getBlue()));
        this.colorInputBlue.setResponder(_ -> onColorChange(getColorFromRGBInputs()));

        // buttons
        LinearLayout buttonRow = horizontal().spacing(SPACING_HORIZONTAL);
        buttonRow.addChild(Button.builder(GUI_CANCEL, _ -> this.minecraft.setScreen(this.backgroundScreen)).width(144).build());
        buttonRow.addChild(Button.builder(GUI_DONE, _ -> onDone()).width(144).build());
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

    private void onDone() {
        Color colorFromRGBInputs = getColorFromRGBInputs();
        this.onClose.accept(colorFromRGBInputs);
        onClose();
    }

    private void onColorChange(@NonNull Color color) {
        // update button focus
        this.layout.visitWidgets(abstractWidget -> {
            if (abstractWidget instanceof ColorButton colorButton) {
                colorButton.setFocused(color.equals(colorButton.getColor()));
            }
        });

        // update RGB colors
        String colorRedValue = valueOf(color.getRed());
        String colorGreenValue = valueOf(color.getGreen());
        String colorBlueValue = valueOf(color.getBlue());

        if (!this.colorInputRed.getValue().equals(colorRedValue)) {
            this.colorInputRed.setValue(colorRedValue);
        }

        if (!this.colorInputGreen.getValue().equals(colorGreenValue)) {
            this.colorInputGreen.setValue(colorGreenValue);
        }

        if (!this.colorInputBlue.getValue().equals(colorBlueValue)) {
            this.colorInputBlue.setValue(colorBlueValue);
        }
    }

    private @NonNull Color getColorFromRGBInputs() {
        int colorValueRed = extractColorValue(this.colorInputRed);
        int colorValueGreen = extractColorValue(this.colorInputGreen);
        int colorValueBlue = extractColorValue(this.colorInputBlue);
        return new Color(colorValueRed, colorValueGreen, colorValueBlue);
    }

    private int extractColorValue(EditBox editBox) {
        try {
            int i = parseInt(editBox.getValue());
            return clamp(i, 0, 255);
        } catch (Exception e) {
            return 0;
        }
    }
}
