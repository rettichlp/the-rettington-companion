package de.rettichlp.therettingtoncompanion.services;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.SLOT_LOCK_KEY;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC;
import static net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP;
import static net.minecraft.world.entity.player.Inventory.INVENTORY_SIZE;
import static net.minecraft.world.entity.player.Inventory.SLOT_OFFHAND;
import static net.minecraft.world.entity.player.Inventory.isHotbarSlot;
import static net.minecraft.world.inventory.ContainerInput.PICKUP_ALL;
import static net.minecraft.world.inventory.ContainerInput.SWAP;
import static net.minecraft.world.item.ItemStack.isSameItemSameComponents;

public class InventoryService {

    private int hotbarSlotIndex;
    private ItemStack itemStack;
    private boolean slotLockKeyDown;

    public void checkRestock() {
        int currentHotbarSlotIndex = player.getInventory().getSelectedSlot();
        ItemStack currentItemStack = player.getMainHandItem().copy();

        if (this.itemStack == null || this.hotbarSlotIndex != currentHotbarSlotIndex) {
            this.hotbarSlotIndex = currentHotbarSlotIndex;
            this.itemStack = currentItemStack;
            return;
        }

        if (!this.itemStack.isEmpty() && currentItemStack.isEmpty() && restock(this.itemStack)) {
            Minecraft.getInstance().getSoundManager().play(forUI(ARMOR_EQUIP_GENERIC.value(), 1f, 2f));
            Component message = translatable("trc.message.auto_restock.restock_succeeded", this.itemStack.getHoverName());
            player.sendOverlayMessage(message);
        }

        this.itemStack = currentItemStack;
    }

    public boolean restock(@NonNull ItemStack previousItemStack) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.gui.screen() != null || !configuration.inventory().isAutoRestock()) {
            LOGGER.debug("Auto restock is disabled or a screen is open");
            return false;
        }

        MultiPlayerGameMode gameMode = minecraft.gameMode;

        List<Integer> matchingSlotIds = getMatchingSlotIds(previousItemStack);
        if (gameMode == null || matchingSlotIds.isEmpty()) {
            LOGGER.debug("No matching item stacks found");
            return false;
        }

        int mostMatchingSlotIndex = matchingSlotIds.getFirst();

        // adjust slot ids for hotbar
        if (isHotbarSlot(mostMatchingSlotIndex)) {
            mostMatchingSlotIndex += INVENTORY_SIZE;
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
            if (!is.getHoverName().equals(itemStack.getHoverName())) {
                continue;
            }

            matchingSlotIds.add(i);
        }

        return matchingSlotIds;
    }

    public boolean handleSlotLockKey(KeyEvent event, Slot slot) {
        if (!SLOT_LOCK_KEY.matches(event)) {
            return false;
        }

        if (this.slotLockKeyDown) {
            return true;
        }

        this.slotLockKeyDown = true;

        if (slot == null || player == null || slot.container != player.getInventory()) {
            return true;
        }

        int slotIndex = slot.getContainerSlot();
        Set<Integer> lockedSlots = configuration.inventory().getLockedSlots();
        boolean wasLocked = lockedSlots.remove(slotIndex);

        if (!wasLocked) {
            lockedSlots.add(slotIndex);
        }

        configuration.saveToFile();
        Minecraft.getInstance().getSoundManager().play(forUI(EXPERIENCE_ORB_PICKUP, wasLocked ? 0.1f : 1f));

        return true;
    }

    public void releaseSlotLockKey(KeyEvent event) {
        if (SLOT_LOCK_KEY.matches(event)) {
            this.slotLockKeyDown = false;
        }
    }

    public boolean isLockedSlot(int slotIndex) {
        return configuration.inventory().getLockedSlots().contains(slotIndex);
    }

    public boolean isContainerInputLocked(Player inputPlayer, int slotId, int button, ContainerInput input) {
        AbstractContainerMenu menu = inputPlayer.containerMenu;

        if (slotId >= 0 && slotId < menu.slots.size() && isLockedPlayerSlot(menu.getSlot(slotId), inputPlayer)) {
            return true;
        }

        return isLockedSwapTarget(button, input) || picksUpLockedStack(inputPlayer, menu, input);
    }

    private boolean isLockedPlayerSlot(Slot slot, Player inputPlayer) {
        return slot.container == inputPlayer.getInventory() && isLockedSlot(slot.getContainerSlot());
    }

    private boolean isLockedSwapTarget(int button, ContainerInput input) {
        return input == SWAP
                && (isHotbarSlot(button) || button == SLOT_OFFHAND)
                && isLockedSlot(button);
    }

    private boolean picksUpLockedStack(Player inputPlayer, AbstractContainerMenu menu, ContainerInput input) {
        ItemStack carried = menu.getCarried();
        if (input != PICKUP_ALL || carried.isEmpty()) {
            return false;
        }

        return menu.slots.stream()
                .filter(slot -> isLockedPlayerSlot(slot, inputPlayer))
                .anyMatch(slot -> isSameItemSameComponents(slot.getItem(), carried));
    }
}
