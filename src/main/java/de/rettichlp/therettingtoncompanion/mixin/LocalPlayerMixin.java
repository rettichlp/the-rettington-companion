package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.inventoryService;
import static net.minecraft.world.InteractionHand.MAIN_HAND;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void trc$dropHead(boolean all, CallbackInfoReturnable<Boolean> cir) {
        if (inventoryService.isLockedHand((LocalPlayer) (Object) this, MAIN_HAND)) {
            cir.setReturnValue(false);
        }
    }
}
