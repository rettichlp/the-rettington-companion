package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static java.util.Comparator.comparingInt;
import static net.minecraft.screen.slot.SlotActionType.QUICK_MOVE;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Unique
    private Item item;

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void trc$useOnBlockHead(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        this.item = context.getStack().getItem();
    }

    @Inject(method = "useOnBlock", at = @At("RETURN"))
    private void trc$useOnBlockReturn(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        MinecraftClient client = MinecraftClient.getInstance();

        ClientPlayerEntity player = client.player;
        ItemStack currentStack = context.getStack();

        if (player == null || currentStack.getCount() > 0) {
            return;
        }

        player.getInventory().getMainStacks().stream()
                .filter(itemStack -> itemStack.isOf(this.item))
                .min(comparingInt(ItemStack::getCount))
                .ifPresent(itemStack -> {
                    int slotWithStack = player.getInventory().getSlotWithStack(itemStack);
                    // I don't know why, but this puts the item in the selected hotbar slot directly and not on the first free slot
                    client.interactionManager.clickSlot(player.currentScreenHandler.syncId, slotWithStack, 0, QUICK_MOVE, player);
                });
    }
}
