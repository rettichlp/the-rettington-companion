package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.inventoryService;
import static net.minecraft.world.inventory.ContainerInput.PICKUP_ALL;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    @Unique
    private boolean suppressPredictionPacket;

    @Inject(method = "handleContainerInput", at = @At("HEAD"), cancellable = true)
    private void trc$handleContainerInputHead(int containerId, int slotNum, int buttonNum, ContainerInput containerInput, @NonNull Player player, CallbackInfo ci) {
        if (containerId != player.containerMenu.containerId || !inventoryService.affectsLockedSlot(player.containerMenu, slotNum, buttonNum, containerInput, player)) {
            return;
        }

        ci.cancel();

        // the server would also take from locked slots, so collect from the unlocked ones only
        if (containerInput == PICKUP_ALL) {
            inventoryService.pickupAllFromUnlockedSlots((MultiPlayerGameMode) (Object) this, player.containerMenu, buttonNum, player);
        }
    }

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void trc$useItemHead(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (inventoryService.wouldEquipIntoLockedSlot(player, hand)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "performUseItemOn",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;"),
            cancellable = true)
    private void trc$performUseItemOnInvoke(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        // block interactions (e.g. opening a chest) already happened, only the placement is prevented
        if (inventoryService.wouldPlaceFromLockedSlot(player, hand)) {
            this.suppressPredictionPacket = true;
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    // the server would place the block anyway, so the use item on packet must not be sent
    @WrapWithCondition(method = "startPrediction",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private boolean trc$startPredictionSend(ClientPacketListener connection, Packet<?> packet) {
        boolean send = !this.suppressPredictionPacket;
        this.suppressPredictionPacket = false;
        return send;
    }
}
