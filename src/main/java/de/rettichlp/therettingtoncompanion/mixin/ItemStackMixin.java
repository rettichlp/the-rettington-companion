package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.inventoryService;
import static net.minecraft.client.sound.PositionedSoundInstance.ui;
import static net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL;
import static net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE;
import static net.minecraft.sound.SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Unique
    private MinecraftClient client = MinecraftClient.getInstance();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract Item getItem();

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
}
