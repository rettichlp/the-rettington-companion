package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {

    @Unique
    private int settingsButtonX = 0;

    @Unique
    private int settingsButtonY = 0;

    @Unique
    private Button button;

    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (this.button != null && this.button.mouseClicked(event, doubleClick)) {
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Inject(method = "createPauseMenu", at = @At("TAIL"))
    private void trc$createPauseMenuTail(CallbackInfo ci, @Local(name = "gridLayout") @NonNull GridLayout gridLayout) {
        gridLayout.visitChildren(clickableWidget -> {
            if (clickableWidget instanceof Button firstBigButton && firstBigButton.getWidth() == 204 && this.settingsButtonX == 0 && this.settingsButtonY == 0) {
                this.settingsButtonX = firstBigButton.getX() + firstBigButton.getWidth() + 4;
                this.settingsButtonY = firstBigButton.getY();
            }
        });
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void trc$extractRenderStateReturn(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        this.button = Button.builder(empty(), _ -> this.minecraft.gui.setScreen(new TRCOptionsScreen(this)))
                .size(20, 20)
                .build();
        this.button.setPosition(this.settingsButtonX, this.settingsButtonY);
        this.button.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.blit(GUI_TEXTURED, fromNamespaceAndPath(MOD_ID, "icon.png"), this.settingsButtonX + 4, this.settingsButtonY + 4, 0, 0, 12, 12, 12, 12);
    }
}
