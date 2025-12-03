package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.IconButtonWidget;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.ModOptionScreen;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILHARD;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    @Unique
    private int settingsButtonX = 0;

    @Unique
    private int settingsButtonY = 0;

    @Unique
    private ButtonWidget buttonWidget;

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.buttonWidget != null && this.buttonWidget.mouseClicked(click, doubled)) {
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    /**
     * Injects additional functionality into the {@code initWidgets} method of the game menu screen. This method is executed at the
     * "TAIL" point of the original {@code initWidgets} method, allowing for modifications to the widget layout after the base method
     * execution has completed. Specifically, it identifies a button widget that spans two columns of the grid (with a width of 204
     * units) and calculates the position for a secondary settings button, storing its coordinates.
     *
     * @param ci         The {@code CallbackInfo} instance used to control the continuation of the method execution.
     * @param gridWidget The {@code GridWidget} instance that represents the container for UI widgets in the game menu.
     * @param adder      The {@code GridWidget.Adder} instance used to manage widget additions.
     */
    @Inject(method = "initWidgets",
            at = @At("TAIL"), locals = CAPTURE_FAILHARD)
    private void trc$initWidgetsTail(CallbackInfo ci, GridWidget gridWidget, GridWidget.Adder adder) {
        gridWidget.forEachChild(clickableWidget -> {
            if (clickableWidget instanceof ButtonWidget buttonWidget && buttonWidget.getWidth() == 204 && this.settingsButtonX == 0 && this.settingsButtonY == 0) {
                // calculate positions for a button that occupies two columns (204 width) of the grid
                this.settingsButtonX = clickableWidget.getX() + clickableWidget.getWidth() + 4;
                this.settingsButtonY = clickableWidget.getY();
            }
        });
    }

    /**
     * Injects additional functionality into the {@code render} method of the game menu screen. This method is executed after the
     * original rendering process is completed. It adds a custom button to the screen and renders it. The button allows players to
     * access the mod options screen when clicked.
     *
     * @param context    An instance of {@code DrawContext}, used to handle rendering operations.
     * @param mouseX     The current X-coordinate of the mouse cursor.
     * @param mouseY     The current Y-coordinate of the mouse cursor.
     * @param deltaTicks The time elapsed between the previous and current render frame, used for smooth animations and updates.
     * @param ci         A {@code CallbackInfo} instance, provided to manage the continuation of the original render execution flow if
     *                   needed.
     */
    @Inject(method = "render", at = @At("RETURN"))
    private void trc$renderReturn(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        this.buttonWidget = new IconButtonWidget(0, 0, Identifier.of(MOD_ID, "icon.png"), 0, 0, 12, 12, button -> this.client.setScreen(new ModOptionScreen()));
        this.buttonWidget.setPosition(this.settingsButtonX, this.settingsButtonY);
        this.buttonWidget.render(context, mouseX, mouseY, deltaTicks);
    }
}
