package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Consumer;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static java.util.Comparator.comparingInt;
import static net.minecraft.client.sound.PositionedSoundInstance.ui;
import static net.minecraft.screen.slot.SlotActionType.PICKUP;
import static net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL;
import static net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE;
import static net.minecraft.sound.SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract Item getItem();

    @Unique
    private MinecraftClient client = MinecraftClient.getInstance();

    @Inject(method = "useOnBlock", at = @At("RETURN"))
    private void trc$useOnBlockReturn(@NotNull ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getWorld().isClient() && this.client.player != null && context.getStack().getCount() == 0 && configuration.inventory().isAutoRestock()) {
            tryRestock(false);
        }
    }

    @Inject(method = "onDurabilityChange", at = @At("HEAD"))
    private void trc$onDurabilityChange(int damage, ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo ci) {
        if (!configuration.inventory().isAutoRestock()) {
            return;
        }

        int remainingUses = getMaxDamage() - damage;
        switch (remainingUses) {
            case 50 -> {
                player.sendMessage(translatable("trc.message.auto_restock.tool_durability_warning", getItem().getName().getString(), 50).formatted(GOLD), true);
                this.client.getSoundManager().play(ui(BLOCK_NOTE_BLOCK_COW_BELL.value(), 2f, 2f));
            }
            case 25 -> {
                player.sendMessage(translatable("trc.message.auto_restock.tool_durability_warning", getItem().getName().getString(), 25).formatted(GOLD), true);
                this.client.getSoundManager().play(ui(BLOCK_NOTE_BLOCK_COW_BELL.value(), 2f, 2f));
            }
            case 10, 5 -> {
                if (tryRestock(true)) {
                    player.sendMessage(translatable("trc.message.auto_restock.restock_succeeded", getItem().getName().getString()).formatted(GREEN), true);
                    this.client.getSoundManager().play(ui(ITEM_ARMOR_EQUIP_GENERIC.value(), 1f, 2f));
                } else {
                    player.sendMessage(translatable("trc.message.auto_restock.restock_failed", getItem().getName().getString()).formatted(RED), true);
                    this.client.getSoundManager().play(ui(BLOCK_NOTE_BLOCK_COW_BELL.value(), 2f, 2f));
                    this.client.getSoundManager().play(ui(BLOCK_NOTE_BLOCK_IRON_XYLOPHONE.value(), 1f, 2f));
                }
            }
        }
    }

    @Unique
    private boolean tryRestock(boolean replace) {
        Optional<ItemStack> optionalItemStack = getMatchingItemStack();

        if (optionalItemStack.isEmpty()) {
            return false;
        }

        int slotWithStack = player.getInventory().getSlotWithStack(optionalItemStack.get());
        int selectedSlot = player.getInventory().getSelectedSlot() + 36; // translate to screen handler slot index

        if (replace) {
            this.client.interactionManager.clickSlot(player.currentScreenHandler.syncId, selectedSlot, 0, PICKUP, player);
        }

        this.client.interactionManager.clickSlot(player.currentScreenHandler.syncId, slotWithStack, 0, PICKUP, player);
        this.client.interactionManager.clickSlot(player.currentScreenHandler.syncId, selectedSlot, 0, PICKUP, player);

        return true;
    }

    @Unique
    private @NotNull Optional<ItemStack> getMatchingItemStack() {
        DefaultedList<ItemStack> mainStacks = player.getInventory().getMainStacks();
        return mainStacks.subList(9, mainStacks.size()).stream()
                .filter(itemStack -> itemStack.isOf(getItem()))
                .filter(itemStack -> !itemStack.isDamageable() || itemStack.getMaxDamage() - itemStack.getDamage() > 10)
                .min(comparingInt(ItemStack::getCount));
    }
}
