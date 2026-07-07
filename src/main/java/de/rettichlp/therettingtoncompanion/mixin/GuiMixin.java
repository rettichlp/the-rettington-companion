package de.rettichlp.therettingtoncompanion.mixin;

import com.mojang.blaze3d.platform.Window;
import de.rettichlp.therettingtoncompanion.gui.screens.WidgetPositionScreen;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.NonNull;
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
import java.util.HashSet;
import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.function.Predicate;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.inventoryService;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.renderService;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.widgetService;
import static de.rettichlp.therettingtoncompanion.utils.ModUtils.colorFromChatFormatting;
import static java.lang.String.valueOf;
import static net.minecraft.ChatFormatting.DARK_AQUA;
import static net.minecraft.ChatFormatting.DARK_GRAY;
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
    @Final
    private Minecraft minecraft;

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
    private void trc$extractRenderStateTail(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.screen instanceof WidgetPositionScreen) {
            // draw grid
            int squareSize = configuration.widgets().getWidgetPositionScale(this.minecraft);

            for (Integer snapPosition : getSnapPositions(graphics.guiWidth(), squareSize)) {
                graphics.verticalLine(snapPosition, -1, graphics.guiHeight(), colorFromChatFormatting(DARK_GRAY).getRGB());
            }

            for (Integer snapPosition : getSnapPositions(graphics.guiHeight(), squareSize)) {
                graphics.horizontalLine(-1, graphics.guiWidth(), snapPosition, colorFromChatFormatting(DARK_GRAY).getRGB());
            }

            graphics.verticalLine(graphics.guiWidth() / 2, -1, graphics.guiHeight(), colorFromChatFormatting(DARK_AQUA).getRGB());
            graphics.horizontalLine(-1, graphics.guiWidth(), graphics.guiHeight() / 2, colorFromChatFormatting(DARK_AQUA).getRGB());
        }

        // render widgets
        Profiler.get().push("widget");
        widgetService.getWidgets().forEach(abstractWidget -> abstractWidget.extractWidget(graphics));
        Profiler.get().pop();

        // render empty inventory space text
        GameType gameType = player.gameMode();
        if (configuration.visuals().isShowEmptyInventorySlotCount() && gameType != null && gameType.isSurvival()) {
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
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;

        if (localPlayer == null) {
            return;
        }

        inventoryService.checkRestock();

        Window window = minecraft.getWindow();
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
        Collection<ItemStack> arrowItems = new ArrayList<>(minecraft.player.getInventory().getNonEquipmentItems().stream()
                .filter(ARROW_ITEM_PREDICATE)
                .toList());

        ItemStack offHandStack = minecraft.player.getOffhandItem();
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
    private void drawArmorHud(@NonNull GuiGraphicsExtractor graphics,
                              DeltaTracker deltaTracker,
                              int y,
                              @NonNull SequencedCollection<ItemStack> armorItems) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        Window window = minecraft.getWindow();

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
    private void drawArrowHud(@NonNull GuiGraphicsExtractor graphics,
                              DeltaTracker deltaTracker,
                              int y,
                              @NonNull Collection<ItemStack> arrowItems) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        Window window = minecraft.getWindow();

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
    private void drawSingleSlot(@NonNull GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blitSprite(GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, x, y - 1, 29, 24); // for some reason y for offhand slot is 23 and not 22 (that's why -1)
    }

    @Unique
    private void drawFirstSlot(@NonNull GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blitSprite(GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 0, 0, x, y, 21, 22);
    }

    @Unique
    private void drawMiddleSlot(@NonNull GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blitSprite(GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 21, 0, x, y, 20, 22);
    }

    @Unique
    private void drawLastSlot(@NonNull GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blitSprite(GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 161, 0, x, y, 21, 22);
    }

    @Unique
    private @NonNull Set<Integer> getSnapPositions(int guiSize, int squareSize) {
        Set<Integer> snapPositions = new HashSet<>();

        for (int i = guiSize / 2; i >= 0; i -= squareSize) {
            snapPositions.add(i);
        }

        for (int i = guiSize / 2; i <= guiSize; i += squareSize) {
            snapPositions.add(i);
        }

        return snapPositions;
    }
}
