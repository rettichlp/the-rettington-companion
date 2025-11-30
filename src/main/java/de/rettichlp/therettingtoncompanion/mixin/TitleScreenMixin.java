package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.common.gui.screens.options.ModOptionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_NAME;
import static java.lang.Boolean.getBoolean;
import static net.minecraft.text.Text.literal;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "addDevelopmentWidgets", at = @At("RETURN"), cancellable = true)
    private void trc$addDevelopmentWidgetsReturn(int y, int spacingY, CallbackInfoReturnable<Integer> cir) {
        if (getBoolean("fabric.development")) {
            this.addDrawableChild(ButtonWidget.builder(literal(MOD_NAME), button -> this.client.setScreen(new ModOptionScreen()))
                    .dimensions(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - 100, y += spacingY, 200, 20)
                    .build());
        }

        cir.setReturnValue(y);
    }
}
