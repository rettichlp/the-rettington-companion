package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.inventoryService;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC;
import static net.minecraft.sounds.SoundEvents.NOTE_BLOCK_COW_BELL;
import static net.minecraft.sounds.SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Unique
    private final Minecraft minecraft = Minecraft.getInstance();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract Component getItemName();

    @Inject(method = "applyDamage", at = @At("HEAD"))
    private void trc$applyDamageHead(int newDamage, @Nullable ServerPlayer player, Consumer<Item> onBreak, CallbackInfo ci) {
        if (!configuration.inventory().isAutoRestock()) {
            return;
        }

        int remainingUses = getMaxDamage() - newDamage;
        switch (remainingUses) {
            case 50 -> {
                player.sendOverlayMessage(translatable("trc.message.auto_restock.tool_durability_warning", getItemName().getString(), 50).withStyle(GOLD));
                this.minecraft.getSoundManager().play(forUI(NOTE_BLOCK_COW_BELL.value(), 2f, 2f));
            }
            case 25 -> {
                player.sendOverlayMessage(translatable("trc.message.auto_restock.tool_durability_warning", getItemName().getString(), 25).withStyle(GOLD));
                this.minecraft.getSoundManager().play(forUI(NOTE_BLOCK_COW_BELL.value(), 2f, 2f));
            }
            case 10, 5 -> {
                if (inventoryService.restock((ItemStack) (Object) this)) {
                    player.sendOverlayMessage(translatable("trc.message.auto_restock.restock_succeeded", getItemName().getString()).withStyle(GREEN));
                    this.minecraft.getSoundManager().play(forUI(ARMOR_EQUIP_GENERIC.value(), 1f, 2f));
                } else {
                    player.sendOverlayMessage(translatable("trc.message.auto_restock.restock_failed", getItemName().getString()).withStyle(RED));
                    this.minecraft.getSoundManager().play(forUI(NOTE_BLOCK_COW_BELL.value(), 2f, 2f));
                    this.minecraft.getSoundManager().play(forUI(NOTE_BLOCK_IRON_XYLOPHONE.value(), 1f, 2f));
                }
            }
        }
    }
}
