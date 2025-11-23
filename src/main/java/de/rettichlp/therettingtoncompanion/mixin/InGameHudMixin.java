package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.function.Predicate;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.entity.EquipmentSlot.CHEST;
import static net.minecraft.entity.EquipmentSlot.FEET;
import static net.minecraft.entity.EquipmentSlot.HEAD;
import static net.minecraft.entity.EquipmentSlot.LEGS;
import static net.minecraft.item.Items.ARROW;
import static net.minecraft.item.Items.SPECTRAL_ARROW;
import static net.minecraft.item.Items.TIPPED_ARROW;
import static net.minecraft.util.Arm.RIGHT;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Unique
    private static final Predicate<ItemStack> ARROW_ITEM_PREDICATE = itemStack -> itemStack.isOf(ARROW) || itemStack.isOf(SPECTRAL_ARROW) || itemStack.isOf(TIPPED_ARROW);

    @Shadow
    @Final
    private static Identifier HOTBAR_TEXTURE;

    @Shadow
    @Final
    private static Identifier HOTBAR_OFFHAND_LEFT_TEXTURE;

    @Shadow
    protected abstract void renderHotbarItem(DrawContext context,
                                             int x,
                                             int y,
                                             RenderTickCounter tickCounter,
                                             PlayerEntity player,
                                             ItemStack stack,
                                             int seed);

    @Inject(method = "renderHotbar", at = @At("TAIL"))
    private void renderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity playerEntity = client.player;

        if (playerEntity == null) {
            return;
        }

        Window window = client.getWindow();
        int y = window.getScaledHeight() - 22;

        // draw armor hud
        List<ItemStack> armorItems = List.of(
                playerEntity.getEquippedStack(HEAD),
                playerEntity.getEquippedStack(CHEST),
                playerEntity.getEquippedStack(LEGS),
                playerEntity.getEquippedStack(FEET)
        );

        if (!armorItems.stream().allMatch(ItemStack::isEmpty) && configuration.isShowArmorHud()) {
            drawArmorHud(context, tickCounter, y, armorItems);
        }

        // draw arrow hud
        Collection<ItemStack> arrowItems = new ArrayList<>(client.player.getInventory().getMainStacks().stream()
                .filter(ARROW_ITEM_PREDICATE)
                .toList());

        ItemStack offHandStack = client.player.getOffHandStack();
        if (ARROW_ITEM_PREDICATE.test(offHandStack)) {
            arrowItems.add(offHandStack);
        }

        if (!arrowItems.isEmpty() && configuration.isShowArrowHud()) {
            drawArrowHud(context, tickCounter, y, arrowItems);
        }
    }

    @Unique
    private void drawArmorHud(@NotNull DrawContext context,
                              RenderTickCounter tickCounter,
                              int y,
                              @NotNull SequencedCollection<ItemStack> armorItems) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity playerEntity = client.player;
        Window window = client.getWindow();

        assert playerEntity != null; // cannot be null at this point
        int armorSlotsX = playerEntity.getMainArm() == RIGHT
                ? window.getScaledWidth() / 2 /* half screen width */ + 91 /* half hotbar width */ + 7 /* space */
                : window.getScaledWidth() / 2 /* half screen width */ - 91 /* half hotbar width */ - 7 /* space */ - 82 /* armor hud width */;

        // draw background: 4 slots (first, 2 middle, last)
        drawFirstSlot(context, armorSlotsX, y); // width = 21
        drawMiddleSlot(context, armorSlotsX + 21, y); // width = 20
        drawMiddleSlot(context, armorSlotsX + 41, y); // width = 20
        drawLastSlot(context, armorSlotsX + 61, y); // width = 21

        // draw items
        int itemStackIndex = 0;
        for (ItemStack itemStack : armorItems.reversed()) {
            int itemStackX = armorSlotsX + itemStackIndex * 20 + 3;
            int itemStackY = y + 3;

            renderHotbarItem(context, itemStackX, itemStackY, tickCounter, playerEntity, itemStack, itemStackIndex++);
        }
    }

    @Unique
    private void drawArrowHud(@NotNull DrawContext context,
                              RenderTickCounter tickCounter,
                              int y,
                              @NotNull Collection<ItemStack> arrowItems) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity playerEntity = client.player;
        Window window = client.getWindow();

        int arrowItemsAmount = arrowItems.size();

        assert playerEntity != null; // cannot be null at this point
        int arrowSlotsX = playerEntity.getMainArm() == RIGHT
                ? window.getScaledWidth() / 2 /* half screen width */ - 91 /* half hotbar width */ - 29 /* offhand slot width */ - 7 /* space */ - (arrowItemsAmount * 20 + 2) /* arrow hud width */
                : window.getScaledWidth() / 2 /* half screen width */ + 91 /* half hotbar width */ + 29 /* offhand slot width */ + 7 /* space */;

        // draw background
        if (arrowItemsAmount == 1) {
            drawSingleSlot(context, arrowSlotsX, y);
        } else {
            drawFirstSlot(context, arrowSlotsX, y); // width = 21

            int currentX = arrowSlotsX + 21;
            for (int i = 0; i < arrowItemsAmount - 2; i++) {
                drawMiddleSlot(context, currentX, y);
                currentX += 20;
            }

            drawLastSlot(context, currentX, y); // width = 21
        }

        // draw items
        int itemStackIndex = 0;
        for (ItemStack itemStack : arrowItems) {
            int itemStackX = arrowSlotsX + itemStackIndex * 20 + 3;
            int itemStackY = y + 3;

            renderHotbarItem(context, itemStackX, itemStackY, tickCounter, playerEntity, itemStack, itemStackIndex++);
        }
    }

    @Unique
    private void drawSingleSlot(@NotNull DrawContext context, int x, int y) {
        context.drawGuiTexture(GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_TEXTURE, x, y - 1, 29, 24); // for some reason y for offhand slot is 23 and not 22 (that's why -1)
    }

    @Unique
    private void drawFirstSlot(@NotNull DrawContext context, int x, int y) {
        context.drawGuiTexture(GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 0, 0, x, y, 21, 22);
    }

    @Unique
    private void drawMiddleSlot(@NotNull DrawContext context, int x, int y) {
        context.drawGuiTexture(GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 21, 0, x, y, 20, 22);
    }

    @Unique
    private void drawLastSlot(@NotNull DrawContext context, int x, int y) {
        context.drawGuiTexture(GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 161, 0, x, y, 21, 22);
    }
}
