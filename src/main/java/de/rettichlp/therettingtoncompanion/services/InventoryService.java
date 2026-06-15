package de.rettichlp.therettingtoncompanion.services;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC;
import static net.minecraft.world.inventory.ContainerInput.SWAP;

public class InventoryService {

    private int hotbarSlotIndex;
    private ItemStack itemStack;

    public void checkRestock() {
        int currentHotbarSlotIndex = player.getInventory().getSelectedSlot();
        ItemStack currentItemStack = player.getMainHandItem().copy();

        if (this.itemStack == null || this.hotbarSlotIndex != currentHotbarSlotIndex) {
            this.hotbarSlotIndex = currentHotbarSlotIndex;
            this.itemStack = currentItemStack;
            return;
        }

        if (!this.itemStack.isEmpty() && currentItemStack.isEmpty()) {
            if (restock(this.itemStack)) {
                Minecraft.getInstance().getSoundManager().play(forUI(ARMOR_EQUIP_GENERIC.value(), 1f, 2f));
                Component message = translatable("trc.message.auto_restock.restock_succeeded", this.itemStack.getDisplayName());
                player.sendOverlayMessage(message);
            }
        }

        this.itemStack = currentItemStack;
    }

    public boolean restock(@NonNull ItemStack previousItemStack) {
        Minecraft client = Minecraft.getInstance();

        if (client.screen != null || !configuration.inventory().isAutoRestock()) {
            LOGGER.debug("Auto restock is disabled or a screen is open");
            return false;
        }

        MultiPlayerGameMode gameMode = client.gameMode;

        List<Integer> matchingSlotIds = getMatchingSlotIds(previousItemStack);
        if (gameMode == null || matchingSlotIds.isEmpty()) {
            LOGGER.debug("No matching item stacks found");
            return false;
        }

        int mostMatchingSlotIndex = matchingSlotIds.getFirst();

        // adjust slot ids for hotbar
        if (mostMatchingSlotIndex < 9) {
            mostMatchingSlotIndex += 36;
        }

        gameMode.handleContainerInput(player.containerMenu.containerId, mostMatchingSlotIndex, this.hotbarSlotIndex, SWAP, player);

        return true;
    }

    public List<Integer> getMatchingSlotIds(ItemStack itemStack) {
        List<Integer> matchingSlotIds = new ArrayList<>();
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getNonEquipmentItems().size(); i++) {
            ItemStack is = inventory.getNonEquipmentItems().get(i);

            // skip current selected slot
            if (i == inventory.getSelectedSlot()) {
                continue;
            }

            // check for same type
            if (!is.is(itemStack.getItem())) {
                continue;
            }

            // if item damageable check for more than 10 durability
            if (is.isDamageableItem() && is.getMaxDamage() - is.getDamageValue() <= 10) {
                continue;
            }

            // check for same name
            if (!is.getDisplayName().equals(itemStack.getDisplayName())) {
                continue;
            }

            matchingSlotIds.add(i);
        }

        return matchingSlotIds;
    }
}
