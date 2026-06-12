package de.rettichlp.therettingtoncompanion.mixin;

import com.mojang.blaze3d.platform.Window;
import de.rettichlp.therettingtoncompanion.TheRettingtonCompanionApi;
import de.rettichlp.therettingtoncompanion.common.models.Notification;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.function.Predicate;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.inventoryService;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.notificationService;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.renderService;
import static java.lang.String.valueOf;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.world.entity.EquipmentSlot.CHEST;
import static net.minecraft.world.entity.EquipmentSlot.FEET;
import static net.minecraft.world.entity.EquipmentSlot.HEAD;
import static net.minecraft.world.entity.EquipmentSlot.LEGS;
import static net.minecraft.world.entity.HumanoidArm.RIGHT;
import static net.minecraft.world.item.Items.ARROW;
import static net.minecraft.world.item.Items.SPECTRAL_ARROW;
import static net.minecraft.world.item.Items.TIPPED_ARROW;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Unique
    private static final Predicate<ItemStack> ARROW_ITEM_PREDICATE = itemStack -> itemStack.is(ARROW) || itemStack.is(SPECTRAL_ARROW) || itemStack.is(TIPPED_ARROW);

    @Shadow
    @Final
    private static Identifier HOTBAR_SPRITE;

    @Shadow
    @Final
    private static Identifier HOTBAR_OFFHAND_LEFT_SPRITE;

    @Shadow
    public abstract Font getFont();

    @Shadow
    protected abstract void extractSlot(GuiGraphicsExtractor graphics,
                                        int x,
                                        int y,
                                        DeltaTracker deltaTracker,
                                        Player player,
                                        ItemStack itemStack,
                                        int seed);

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void trc$renderMainHudTail(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        // render notification widgets
        FabricLoader.getInstance() // load notifications from other mods
                .getEntrypointContainers(MOD_ID, TheRettingtonCompanionApi.class)
                .forEach(entrypointContainer -> {
                    TheRettingtonCompanionApi entrypoint = entrypointContainer.getEntrypoint();
                    Set<Notification> externalNotifications = entrypoint.getNotifications();
                    notificationService.getNotifications().addAll(externalNotifications);
                });

//        List<NotificationWidget> notificationWidgets = notificationService.getVisibleNotifications().stream()
//                .map(Notification::toWidget)
//                .toList();
//
//        for (int i = 0; i < notificationWidgets.size(); i++) {
//            AbstractProgressStringWidget<?> abstractProgressStringWidget = notificationWidgets.get(i);
//            int x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - abstractProgressStringWidget.getWidth() - 4;
//            int y = 19 * i + 4;
//            abstractProgressStringWidget.draw(graphics, x, y, AbstractWidget.Alignment.RIGHT);
//        }
//
//        // render widgets
//        renderService.getWidgets().forEach(abstractWidget -> abstractWidget.draw(graphics));

        // render empty inventory space text
        if (configuration.visuals().isShowEmptyInventorySlotCount() && player.gameMode() != null && player.gameMode().isSurvival()) {
            ItemStack mainHandStack = player.getMainHandItem();
            boolean showSameItemLeftAmount = player.isCrouching() && !mainHandStack.isEmpty();
            long emptySlotAmount = showSameItemLeftAmount
                    ? inventoryService.getMatchingSlotIds(mainHandStack).size() // retrieve same-item-left number
                    : player.getInventory().getNonEquipmentItems().stream().filter(ItemStack::isEmpty).count();

            Component text = literal(valueOf(emptySlotAmount));
            int y = graphics.guiHeight() - 46;
            boolean onlyFiveLeft = !showSameItemLeftAmount && emptySlotAmount <= 5;
            renderService.renderShadowText(graphics, text, y, configuration.visuals().getExperienceLevelColor(), onlyFiveLeft ? -6946816 : -16777216);
        }
    }

    @Inject(method = "extractHotbarAndDecorations", at = @At("TAIL"))
    private void trc$extractHotbarAndDecorationsTail(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer localPlayer = client.player;

        if (localPlayer == null) {
            return;
        }

        inventoryService.checkRestock();

        Window window = client.getWindow();
        int y = window.getGuiScaledHeight() - 22;

        // draw armor hud
        List<ItemStack> armorItems = List.of(
                localPlayer.getItemBySlot(HEAD),
                localPlayer.getItemBySlot(CHEST),
                localPlayer.getItemBySlot(LEGS),
                localPlayer.getItemBySlot(FEET)
        );

        if (!armorItems.stream().allMatch(ItemStack::isEmpty) && configuration.visuals().isShowArmorHud()) {
            drawArmorHud(graphics, deltaTracker, y, armorItems);
        }

        // draw arrow hud
        Collection<ItemStack> arrowItems = new ArrayList<>(client.player.getInventory().getNonEquipmentItems().stream()
                .filter(ARROW_ITEM_PREDICATE)
                .toList());

        ItemStack offHandStack = client.player.getOffhandItem();
        if (ARROW_ITEM_PREDICATE.test(offHandStack)) {
            arrowItems.add(offHandStack);
        }

        if (!arrowItems.isEmpty() && configuration.visuals().isShowArrowHud()) {
            drawArrowHud(graphics, deltaTracker, y, arrowItems);
        }
    }

    @ModifyVariable(method = "extractPlayerHealth", at = @At("STORE"), name = "vehicleHearts")
    private int trc$renderStatusBarsStore(int vehicleHearts) {
        // always render food
        return 0;
    }

    @ModifyVariable(method = "extractVehicleHealth", at = @At("STORE"), name = "yo")
    private int trc$extractVehicleHealthStore(int yo) {
        // move mount health bar one row higher
        return yo - 10;
    }

    @Unique
    private void drawArmorHud(@NotNull GuiGraphicsExtractor graphics,
                              DeltaTracker deltaTracker,
                              int y,
                              @NotNull SequencedCollection<ItemStack> armorItems) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer localPlayer = client.player;
        Window window = client.getWindow();

        assert localPlayer != null; // cannot be null at this point
        int armorSlotsX = localPlayer.getMainArm() == RIGHT
                ? window.getGuiScaledWidth() / 2 /* half screen width */ + 91 /* half hotbar width */ + 7 /* space */
                : window.getGuiScaledWidth() / 2 /* half screen width */ - 91 /* half hotbar width */ - 7 /* space */ - 82 /* armor hud width */;

        // draw background: 4 slots (first, 2 middle, last)
        drawFirstSlot(graphics, armorSlotsX, y); // width = 21
        drawMiddleSlot(graphics, armorSlotsX + 21, y); // width = 20
        drawMiddleSlot(graphics, armorSlotsX + 41, y); // width = 20
        drawLastSlot(graphics, armorSlotsX + 61, y); // width = 21

        // draw items
        int itemStackIndex = 0;
        for (ItemStack itemStack : armorItems.reversed()) {
            int itemStackX = armorSlotsX + itemStackIndex * 20 + 3;
            int itemStackY = y + 3;

            extractSlot(graphics, itemStackX, itemStackY, deltaTracker, localPlayer, itemStack, itemStackIndex++);
        }
    }

    @Unique
    private void drawArrowHud(@NotNull GuiGraphicsExtractor graphics,
                              DeltaTracker deltaTracker,
                              int y,
                              @NotNull Collection<ItemStack> arrowItems) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer localPlayer = client.player;
        Window window = client.getWindow();

        int arrowItemsAmount = arrowItems.size();

        assert localPlayer != null; // cannot be null at this point
        int arrowSlotsX = localPlayer.getMainArm() == RIGHT
                ? window.getGuiScaledWidth() / 2 /* half screen width */ - 91 /* half hotbar width */ - 29 /* offhand slot width */ - 7 /* space */ - (arrowItemsAmount * 20 + 2) /* arrow hud width */
                : window.getGuiScaledWidth() / 2 /* half screen width */ + 91 /* half hotbar width */ + 29 /* offhand slot width */ + 7 /* space */;

        // draw background
        if (arrowItemsAmount == 1) {
            drawSingleSlot(graphics, arrowSlotsX, y);
        } else {
            drawFirstSlot(graphics, arrowSlotsX, y); // width = 21

            int currentX = arrowSlotsX + 21;
            for (int i = 0; i < arrowItemsAmount - 2; i++) {
                drawMiddleSlot(graphics, currentX, y);
                currentX += 20;
            }

            drawLastSlot(graphics, currentX, y); // width = 21
        }

        // draw items
        int itemStackIndex = 0;
        for (ItemStack itemStack : arrowItems) {
            int itemStackX = arrowSlotsX + itemStackIndex * 20 + 3;
            int itemStackY = y + 3;

            extractSlot(graphics, itemStackX, itemStackY, deltaTracker, localPlayer, itemStack, itemStackIndex++);
        }
    }

    @Unique
    private void drawSingleSlot(@NotNull GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blitSprite(GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, x, y, 29, 24); // for some reason y for offhand slot is 23 and not 22 (that's why -1)
    }

    @Unique
    private void drawFirstSlot(@NotNull GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blit(GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 0, 0, x, y, 21, 22);
    }

    @Unique
    private void drawMiddleSlot(@NotNull GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blit(GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 21, 0, x, y, 20, 22);
    }

    @Unique
    private void drawLastSlot(@NotNull GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blit(GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 161, 0, x, y, 21, 22);
    }
}
