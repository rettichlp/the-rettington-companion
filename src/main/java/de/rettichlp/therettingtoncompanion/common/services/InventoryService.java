package de.rettichlp.therettingtoncompanion.common.services;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static net.minecraft.client.sound.PositionedSoundInstance.ui;
import static net.minecraft.screen.slot.SlotActionType.SWAP;
import static net.minecraft.sound.SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
import static net.minecraft.text.Text.translatable;

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
            if (restock(this.itemStack)) {
                MinecraftClient.getInstance().getSoundManager().play(ui(ITEM_ARMOR_EQUIP_GENERIC.value(), 1f, 2f));
                Text message = translatable("trc.message.auto_restock.restock_succeeded", this.itemStack.getName());
                player.sendMessage(message, true);
            }
        }

        this.itemStack = currentItemStack;
    }

    public boolean restock(@NonNull ItemStack previousItemStack) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.currentScreen != null || !configuration.inventory().isAutoRestock()) {
            LOGGER.debug("Auto restock is disabled or a screen is open");
            return false;
        }

        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        List<Integer> matchingSlotIds = getMatchingSlotIds(previousItemStack);
        if (interactionManager == null || matchingSlotIds.isEmpty()) {
            LOGGER.debug("No matching item stacks found");
            return false;
        }

        int mostMatchingSlotIndex = matchingSlotIds.getFirst();

        // adjust slot ids for hotbar
        if (mostMatchingSlotIndex < 9) {
            mostMatchingSlotIndex += 36;
        }

        interactionManager.clickSlot(player.currentScreenHandler.syncId, mostMatchingSlotIndex, this.hotbarSlotIndex, SWAP, player);

        return true;
    }

    public List<Integer> getMatchingSlotIds(ItemStack itemStack) {
        List<Integer> matchingSlotIds = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getMainStacks().size(); i++) {
            ItemStack is = inventory.getMainStacks().get(i);

            // skip current selected slot
            if (i == inventory.getSelectedSlot()) {
                continue;
            }

            // check for same type
            if (!is.isOf(itemStack.getItem())) {
                continue;
            }

            // if item damageable check for more than 10 durability
            if (is.isDamageable() && is.getMaxDamage() - is.getDamage() <= 10) {
                continue;
            }

            // check for same name
            if (!is.getName().equals(itemStack.getName())) {
                continue;
            }

            matchingSlotIds.add(i);
        }

        return matchingSlotIds;
    }
}
