package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.inventoryService;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @ModifyExpressionValue(method = "moveItemStackTo",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean trc$moveItemStackToInvoke(boolean sameItem, @Local(name = "slot") Slot slot) {
        return sameItem && !inventoryService.isLockedSlot(slot);
    }

    @ModifyExpressionValue(method = "doClick",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z",
                                    ordinal = 1))
    private boolean trc$doClickInvoke(boolean sameItem, @Local(name = "slot") Slot slot) {
        return sameItem && !inventoryService.isLockedSlot(slot);
    }
}
