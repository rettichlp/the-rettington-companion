package de.rettichlp.therettingtoncompanion.gui.screens;

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

import java.util.function.Consumer;

import static de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen.SPACING_HORIZONTAL;
import static de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen.SPACING_VERTICAL;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static net.minecraft.client.gui.layouts.FrameLayout.centerInRectangle;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT;
import static net.minecraft.network.chat.CommonComponents.GUI_CANCEL;
import static net.minecraft.network.chat.CommonComponents.GUI_DONE;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.TextColor.GRAY;
import static net.minecraft.resources.Identifier.parse;

public class SoundSelectionPopupScreen extends Screen {

    private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("popup/background");

    private final LinearLayout layout = vertical().spacing(SPACING_VERTICAL);

    private final @Nullable Screen backgroundScreen;
    private final @Nullable Identifier initialSound;
    private final Consumer<String> onClose;

    private EditBox input;

    public SoundSelectionPopupScreen(@Nullable Screen backgroundScreen, @Nullable Identifier initialSound, Consumer<String> onClose) {
        super(literal("sound"));
        this.backgroundScreen = backgroundScreen;
        this.initialSound = initialSound;
        this.onClose = onClose;
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.backgroundScreen);
    }

    @Override
    protected void init() {
        if (this.backgroundScreen != null) {
            this.backgroundScreen.init(this.width, this.height);
        }

        this.layout.newCellSettings().alignHorizontallyCenter();
        this.input = this.layout.addChild(new EditBox(this.font, 296, 20, empty()), LayoutSettings::alignHorizontallyCenter);
        this.input.setValue(this.initialSound != null ? this.initialSound.toString() : "");
        this.input.setResponder(value -> this.input.setTextColor((value.isBlank() || isValidSoundIdentifier(value) ? WHITE : RED).getRGB()));

        this.layout.addChild(new StringWidget(translatable("trc.option.chat.filtered_messages.popup.sound.hint").withStyle(style -> style.withColor(GRAY).withItalic(true)), this.font), LayoutSettings::alignHorizontallyCenter);

        // buttons
        LinearLayout buttonRow = horizontal().spacing(SPACING_HORIZONTAL);
        buttonRow.addChild(Button.builder(GUI_CANCEL, _ -> this.minecraft.gui.setScreen(this.backgroundScreen)).width(144).build());
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
        this.onClose.accept(this.input.getValue());
        onClose();
    }

    public static boolean isValidSoundIdentifier(@Nullable String identifierString) {
        if (identifierString == null) {
            return false;
        }

        Identifier parsed = parse(identifierString);
        return SOUND_EVENT.containsKey(parsed);
    }
}
