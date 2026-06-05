package de.rettichlp.therettingtoncompanion.common.services;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static java.util.Comparator.comparingInt;
import static net.minecraft.screen.slot.SlotActionType.SWAP;

public class InventoryService {

    private int hotbarSlotIndex;
    private ItemStack itemStack;

    public void checkRestock() {
        int currentHotbarSlotIndex = player.getInventory().getSelectedSlot();
        ItemStack currentItemStack = player.getMainHandStack().copy();

        if (this.hotbarSlotIndex != currentHotbarSlotIndex) {
            this.hotbarSlotIndex = currentHotbarSlotIndex;
            this.itemStack = currentItemStack;
            return;
        }

        if (!this.itemStack.isEmpty() && currentItemStack.isEmpty()) {
            restock(this.itemStack);
        }

        this.itemStack = currentItemStack;
    }

    public boolean restock(@NonNull ItemStack previousItemStack) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.currentScreen != null || !configuration.inventory().isAutoRestock()) {
            return false;
        }

        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        ItemStack mostMatchingItemStack = getMostMatchingItemStack(previousItemStack);
        if (mostMatchingItemStack == null || interactionManager == null) {
            return false;
        }

        int slotWithStack = player.getInventory().getSlotWithStack(mostMatchingItemStack);
        int hotbarIndex = player.getInventory().getSelectedSlot();

        interactionManager.clickSlot(player.currentScreenHandler.syncId, slotWithStack, hotbarIndex, SWAP, player);
        return true;
    }

    private @Nullable ItemStack getMostMatchingItemStack(ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();

        List<ItemStack> itemStacks = inventory.getMainStacks().stream()
                .filter(is -> inventory.getMainStacks().indexOf(is) != inventory.getSelectedSlot())
                .filter(is -> is.isOf(itemStack.getItem()))
                .filter(is -> !is.isDamageable() || is.getMaxDamage() - is.getDamage() > 10)
                .sorted(comparingInt(ItemStack::getCount))
                .toList();

        if (itemStacks.isEmpty()) {
            return null;
        }

        return itemStacks.stream()
                .filter(is -> is.getName().getString().equals(itemStack.getName().getString()))
                .findFirst()
                .orElseGet(itemStacks::getFirst);
    }
}
