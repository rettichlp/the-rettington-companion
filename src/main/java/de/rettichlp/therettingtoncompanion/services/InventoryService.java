package de.rettichlp.therettingtoncompanion.services;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;
import static net.minecraft.core.component.DataComponents.EQUIPPABLE;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC;
import static net.minecraft.world.entity.player.Inventory.EQUIPMENT_SLOT_MAPPING;
import static net.minecraft.world.entity.player.Inventory.SLOT_OFFHAND;
import static net.minecraft.world.inventory.AbstractContainerMenu.canItemQuickReplace;
import static net.minecraft.world.inventory.ContainerInput.CLONE;
import static net.minecraft.world.inventory.ContainerInput.PICKUP;
import static net.minecraft.world.inventory.ContainerInput.PICKUP_ALL;
import static net.minecraft.world.inventory.ContainerInput.QUICK_MOVE;
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
                Component message = translatable("trc.message.auto_restock.restock_succeeded", this.itemStack.getHoverName());
                player.sendOverlayMessage(message);
            }
        }

        this.itemStack = currentItemStack;
    }

    public boolean restock(@NonNull ItemStack previousItemStack) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.gui.screen() != null || !configuration.inventory().isAutoRestock()) {
            LOGGER.debug("Auto restock is disabled or a screen is open");
            return false;
        }

        if (isLockedSlot(this.hotbarSlotIndex)) {
            LOGGER.debug("Selected hotbar slot is locked");
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

            // skip locked slots
            if (isLockedSlot(i)) {
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

    public boolean isLockedSlot(int slotIndex) {
        return isSlotLockActive() && configuration.inventory().getLockedSlots().contains(slotIndex);
    }

    public boolean isLockedSlot(@NonNull Slot slot) {
        return isOwnInventory(slot) && isLockedSlot(slot.getContainerSlot());
    }

    public void toggleSlotLock(@Nullable Slot slot) {
        if (slot == null || !isSlotLockActive() || !isOwnInventory(slot)) {
            return;
        }

        Set<Integer> lockedSlots = configuration.inventory().getLockedSlots();
        int slotIndex = slot.getContainerSlot();
        boolean wasLocked = lockedSlots.remove(slotIndex);

        if (!wasLocked) {
            lockedSlots.add(slotIndex);
        }

        configuration.saveToFile();
        player.sendOverlayMessage(translatable(wasLocked ? "trc.message.slot_lock.unlocked" : "trc.message.slot_lock.locked"));
    }

    // the creative inventory ignores locked slots
    private boolean isSlotLockActive() {
        return !(Minecraft.getInstance().gui.screen() instanceof CreativeModeInventoryScreen);
    }

    // identity check keeps the locks client-side only (the integrated server has its own player instance)
    private boolean isOwnInventory(@NonNull Slot slot) {
        return slot.container instanceof Inventory inventory && inventory.player == player;
    }

    public boolean affectsLockedSlot(@NonNull AbstractContainerMenu menu, int slotNum, int buttonNum, ContainerInput containerInput, Player clickPlayer) {
        if (configuration.inventory().getLockedSlots().isEmpty()) {
            return false;
        }

        // clicked slot itself is locked
        if (containerInput != CLONE && slotNum >= 0 && slotNum < menu.slots.size() && isLockedSlot(menu.getSlot(slotNum))) {
            return true;
        }

        // hotbar or offhand slot used as swap source is locked
        if (containerInput == SWAP && isLockedSlot(buttonNum)) {
            return true;
        }

        // quick move and pick all may touch any slot, so predict what the (unmodded) server would do
        return (containerInput == QUICK_MOVE || containerInput == PICKUP_ALL) && wouldChangeLockedSlot(menu, slotNum, buttonNum, containerInput, clickPlayer);
    }

    private boolean wouldChangeLockedSlot(@NonNull AbstractContainerMenu menu, int slotNum, int buttonNum, ContainerInput containerInput, Player clickPlayer) {
        List<ItemStack> itemsBeforeClick = menu.slots.stream().map(slot -> slot.getItem().copy()).toList();
        ItemStack carriedBeforeClick = menu.getCarried().copy();

        menu.clicked(slotNum, buttonNum, containerInput, clickPlayer);

        boolean lockedSlotChanged = false;
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            ItemStack before = itemsBeforeClick.get(i);
            if (ItemStack.matches(before, slot.getItem())) {
                continue;
            }

            lockedSlotChanged |= isLockedSlot(slot);
            slot.set(before);
        }

        menu.setCarried(carriedBeforeClick);

        return lockedSlotChanged;
    }

    public void pickupAllFromUnlockedSlots(MultiPlayerGameMode gameMode, @NonNull AbstractContainerMenu menu, int buttonNum, Player clickPlayer) {
        // same slot order as vanilla: first non-full stacks, then full stacks
        int start = buttonNum == 0 ? 0 : menu.slots.size() - 1;
        int step = buttonNum == 0 ? 1 : -1;

        for (int pass = 0; pass < 2; pass++) {
            for (int i = start; i >= 0 && i < menu.slots.size(); i += step) {
                ItemStack carried = menu.getCarried();
                if (carried.isEmpty() || carried.getCount() >= carried.getMaxStackSize()) {
                    return;
                }

                Slot slot = menu.slots.get(i);
                ItemStack item = slot.getItem();
                if (isLockedSlot(slot) || !slot.hasItem() || !canItemQuickReplace(slot, carried, true) || !slot.mayPickup(clickPlayer) || !menu.canTakeItemForPickAll(carried, slot)) {
                    continue;
                }

                // plain clicks cannot take a part of a stack, so only take stacks that fit completely
                if ((pass == 0 && item.getCount() == item.getMaxStackSize()) || carried.getCount() + item.getCount() > carried.getMaxStackSize()) {
                    continue;
                }

                // put the carried items onto the slot, then pick up the merged stack
                gameMode.handleContainerInput(menu.containerId, i, 0, PICKUP, clickPlayer);
                gameMode.handleContainerInput(menu.containerId, i, 0, PICKUP, clickPlayer);
            }
        }
    }

    public boolean wouldEquipIntoLockedSlot(@NonNull Player usePlayer, InteractionHand hand) {
        Equippable equippable = usePlayer.getItemInHand(hand).get(EQUIPPABLE);
        if (equippable == null || !equippable.swappable()) {
            return false;
        }

        return isLockedHand(usePlayer, hand) || isLockedSlot(getInventorySlotIndex(equippable.slot()));
    }

    public boolean wouldPlaceFromLockedSlot(@NonNull Player usePlayer, InteractionHand hand) {
        return usePlayer.getItemInHand(hand).getItem() instanceof BlockItem && isLockedHand(usePlayer, hand);
    }

    private boolean isLockedHand(@NonNull Player usePlayer, InteractionHand hand) {
        return isLockedSlot(hand == InteractionHand.MAIN_HAND ? usePlayer.getInventory().getSelectedSlot() : SLOT_OFFHAND);
    }

    private int getInventorySlotIndex(EquipmentSlot equipmentSlot) {
        return EQUIPMENT_SLOT_MAPPING.int2ObjectEntrySet().stream()
                .filter(entry -> entry.getValue() == equipmentSlot)
                .mapToInt(Int2ObjectMap.Entry::getIntKey)
                .findFirst()
                .orElse(-1);
    }
}
