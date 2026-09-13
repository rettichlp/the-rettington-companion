package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.inventoryService;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow
    @Final
    public Container container;

    @Shadow
    public abstract int getContainerSlot();

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void trc$mayPlaceHead(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (isLocked()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void trc$mayPickupHead(Player pickupPlayer, CallbackInfoReturnable<Boolean> cir) {
        if (isLocked()) {
            cir.setReturnValue(false);
        }
    }

    private boolean isLocked() {
        return inventoryService.isLockedSlot(this.container, getContainerSlot());
    }
}
