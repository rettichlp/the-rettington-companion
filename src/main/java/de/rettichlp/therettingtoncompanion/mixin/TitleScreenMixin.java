package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_NAME;
import static java.lang.Boolean.getBoolean;
import static net.minecraft.network.chat.Component.literal;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "createTestWorldButton", at = @At("RETURN"), cancellable = true)
    private void trc$createTestWorldButtonReturn(int topPos, int spacing, CallbackInfoReturnable<Integer> cir) {
        if (getBoolean("fabric.development")) {
            this.addRenderableWidget(Button.builder(literal(MOD_NAME), button -> this.minecraft.setScreen(new TRCOptionsScreen("general", this, true)))
                    .bounds(this.minecraft.getWindow().getGuiScaledWidth() / 2 - 100, topPos += spacing, 200, 20)
                    .build());
        }

        cir.setReturnValue(topPos);
    }
}
