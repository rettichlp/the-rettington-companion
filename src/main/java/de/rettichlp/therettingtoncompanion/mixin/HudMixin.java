package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Window;
import de.rettichlp.therettingtoncompanion.configuration.ChatTab;
import de.rettichlp.therettingtoncompanion.gui.ChatTabButton;
import de.rettichlp.therettingtoncompanion.gui.screens.ChatTabPopupScreen;
import de.rettichlp.therettingtoncompanion.gui.screens.WidgetPositionScreen;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
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

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.CHAT_PEEK_KEY;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.inventoryService;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.widgetService;
import static de.rettichlp.therettingtoncompanion.gui.ChatTabButton.forAddButton;
import static de.rettichlp.therettingtoncompanion.gui.ChatTabButton.forDefaultTab;
import static de.rettichlp.therettingtoncompanion.gui.ChatTabButton.forTab;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.CHAT_TAB_BUTTONS;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.FOCUSED_CHAT_TAB;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.applyFocusedChatTabMessages;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatLeft;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatRight;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatTopHeight;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.lang.String.valueOf;
import static net.minecraft.client.gui.components.ChatComponent.DisplayMode.FOREGROUND;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.TextColor.DARK_AQUA;
import static net.minecraft.network.chat.TextColor.DARK_GRAY;
import static net.minecraft.world.entity.EquipmentSlot.CHEST;
import static net.minecraft.world.entity.EquipmentSlot.FEET;
import static net.minecraft.world.entity.EquipmentSlot.HEAD;
import static net.minecraft.world.entity.EquipmentSlot.LEGS;
import static net.minecraft.world.entity.HumanoidArm.RIGHT;
import static net.minecraft.world.item.Items.ARROW;
import static net.minecraft.world.item.Items.SPECTRAL_ARROW;
import static net.minecraft.world.item.Items.TIPPED_ARROW;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(Hud.class)
public abstract class HudMixin {

    @Unique
    private static final Predicate<ItemStack> ARROW_ITEM_PREDICATE = itemStack -> itemStack.is(ARROW) || itemStack.is(SPECTRAL_ARROW) || itemStack.is(TIPPED_ARROW);

    @Unique
    private static final float EFFECT_HIDDEN_ICON_ALPHA = 0.5F;

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
    protected abstract void extractSlot(GuiGraphicsExtractor graphics,
                                        int x,
                                        int y,
                                        DeltaTracker deltaTracker,
                                        Player player,
                                        ItemStack itemStack,
                                        int seed);

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void trc$extractRenderStateTail(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.gui.screen() instanceof WidgetPositionScreen) {
            // draw grid
            int squareSize = configuration.widgets().getSize();

            for (Integer snapPosition : getSnapPositions(graphics.guiWidth(), squareSize)) {
                graphics.verticalLine(snapPosition, -1, graphics.guiHeight(), DARK_GRAY.getValue() | 0xFF000000);
            }

            for (Integer snapPosition : getSnapPositions(graphics.guiHeight(), squareSize)) {
                graphics.horizontalLine(-1, graphics.guiWidth(), snapPosition, DARK_GRAY.getValue() | 0xFF000000);
            }

            graphics.verticalLine(graphics.guiWidth() / 2, -1, graphics.guiHeight(), DARK_AQUA.getValue() | 0xFF000000);
            graphics.horizontalLine(-1, graphics.guiWidth(), graphics.guiHeight() / 2, DARK_AQUA.getValue() | 0xFF000000);
        }

        // render widgets
        Profiler.get().push("widget");
        widgetService.getInitializedWidgets().keySet().forEach(abstractWidget -> abstractWidget.extractWidget(graphics));
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
            renderShadowText(graphics, text, y, configuration.visuals().getExperienceLevelColor(), onlyFiveLeft ? -6946816 : -16777216);
        }

        // render the chat tab bar every frame regardless of whether the chat screen is focused, since Hud is
        // rendered underneath it either way - this keeps the tab button state/layout in a single place
        updateChatTabButtons(graphics);
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

    @ModifyExpressionValue(method = "extractEffects",
                           at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;showIcon()Z"))
    private boolean trc$modifyExpressionValue$extractEffectsInvoke(boolean showIcon) {
        // optionally render the effect icon, even if the effect has no visible particles
        return showIcon || configuration.visuals().isEffectShowAllIcons();
    }

    @WrapOperation(method = "extractEffects",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void trc$wrapOperation$extractEffectsInvoke(GuiGraphicsExtractor graphics,
                                                        RenderPipeline pipeline,
                                                        Identifier sprite,
                                                        int x,
                                                        int y,
                                                        int width,
                                                        int height,
                                                        Operation<Void> original,
                                                        @Local(name = "instance") @NonNull MobEffectInstance instance) {
        // render the background half-transparent if the effect would normally not show an icon (e.g. particles disabled)
        if (instance.showIcon()) {
            original.call(graphics, pipeline, sprite, x, y, width, height);
        } else {
            graphics.blitSprite(pipeline, sprite, x, y, width, height, ARGB.multiplyAlpha(-1, EFFECT_HIDDEN_ICON_ALPHA));
        }
    }

    @Inject(method = "extractEffects",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V",
                     shift = AFTER))
    private void trc$extractEffectsInvoke(GuiGraphicsExtractor graphics,
                                          DeltaTracker deltaTracker,
                                          CallbackInfo ci,
                                          @Local(name = "x") int x,
                                          @Local(name = "y") int y,
                                          @Local(name = "instance") @NonNull MobEffectInstance instance) {
        if (!configuration.visuals().isEffectShowDurationTimer()) {
            return;
        }

        String durationText = getEffectDurationText(instance);
        if (!durationText.isEmpty()) {
            Component text = literal(durationText);
            Font font = this.minecraft.font;
            renderShadowText(graphics, text, x + 12 - font.width(text) / 2, y + 24 - font.lineHeight - 1, WHITE.getRGB(), BLACK.getRGB());
        }
    }

    @WrapOperation(method = "extractChat",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V"))
    private void trc$extractChatInvoke(ChatComponent instance,
                                       GuiGraphicsExtractor graphics,
                                       Font font,
                                       int ticks,
                                       int mouseX,
                                       int mouseY,
                                       ChatComponent.DisplayMode displayMode,
                                       boolean changeCursorOnInsertions,
                                       @NonNull Operation<Void> original) {
        original.call(instance, graphics, font, ticks, mouseX, mouseY, CHAT_PEEK_KEY.isDown() ? FOREGROUND : displayMode, changeCursorOnInsertions);
    }

    @Unique
    private void updateChatTabButtons(@NonNull GuiGraphicsExtractor graphics) {
        CHAT_TAB_BUTTONS.clear();

        List<ChatTab> chatTabs = configuration.chat().getChatTabs().stream()
                .filter(ChatTab::isAvailableOnCurrentServer)
                .toList();
        Font font = this.minecraft.font;
        boolean chatFocused = this.minecraft.gui.screen() instanceof ChatScreen;

        // a server-bound tab that fell out of scope (e.g. the player switched servers) can't stay focused
        if (FOCUSED_CHAT_TAB != null && !chatTabs.contains(FOCUSED_CHAT_TAB)) {
            setFocusedChatTab(null);
        }

        if (chatFocused) {
            // focused: show every tab (with its full unread badge) plus the default/add buttons, and let clicks
            // through the buttons themselves - ChatScreenMixin dispatches mouse events to this same list
            if (!chatTabs.isEmpty()) {
                CHAT_TAB_BUTTONS.add(forDefaultTab(font, _ -> setFocusedChatTab(null)));
            }

            for (ChatTab chatTab : chatTabs) {
                CHAT_TAB_BUTTONS.add(forTab(font, chatTab, _ -> setFocusedChatTab(FOCUSED_CHAT_TAB == chatTab ? null : chatTab)));
            }

            CHAT_TAB_BUTTONS.add(forAddButton(font, _ -> {
                ChatTab newChatTab = new ChatTab("Tab " + (configuration.chat().getChatTabs().size() + 1));
                configuration.chat().getChatTabs().add(newChatTab);
                this.minecraft.gui.setScreen(new ChatTabPopupScreen(this.minecraft.gui.screen(), newChatTab));
            }));
        } else {
            for (ChatTab chatTab : chatTabs) {
                if (chatTab.getUnreadCount() > 0) {
                    CHAT_TAB_BUTTONS.add(forTab(font, chatTab, _ -> {}));
                }
            }
        }

        if (CHAT_TAB_BUTTONS.isEmpty()) {
            return;
        }

        for (ChatTabButton chatTabButton : CHAT_TAB_BUTTONS) {
            chatTabButton.refresh(font);
        }

        layoutChatTabButtons(CHAT_TAB_BUTTONS);

        for (ChatTabButton chatTabButton : CHAT_TAB_BUTTONS) {
            chatTabButton.draw(graphics, -1, -1, 1.0F);
        }
    }

    @Unique
    private void setFocusedChatTab(@Nullable ChatTab chatTab) {
        // clear the previously focused tab's unread state only once it's actually left, so its unread divider line stays
        // in place for as long as it remains focused
        if (FOCUSED_CHAT_TAB != null && FOCUSED_CHAT_TAB != chatTab) {
            FOCUSED_CHAT_TAB.setUnreadCount(0);
            FOCUSED_CHAT_TAB.setFilterTriggered(false);
        }

        FOCUSED_CHAT_TAB = chatTab;

        // load pre-filtered messages
        applyFocusedChatTabMessages();
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

    @Unique
    private void renderShadowText(@NonNull GuiGraphicsExtractor graphics, Component text, int y, int color, int shadowColor) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);
        renderShadowText(graphics, text, (graphics.guiWidth() - textWidth) / 2, y, color, shadowColor);
    }

    @Unique
    private void renderShadowText(@NonNull GuiGraphicsExtractor graphics, Component text, int x, int y, int color, int shadowColor) {
        Font font = Minecraft.getInstance().font;

        // render shadow
        graphics.text(font, text, x + 1, y, shadowColor, false);
        graphics.text(font, text, x - 1, y, shadowColor, false);
        graphics.text(font, text, x, y + 1, shadowColor, false);
        graphics.text(font, text, x, y - 1, shadowColor, false);

        // render text
        graphics.text(font, text, x, y, color, false);
    }

    @Unique
    private void layoutChatTabButtons(@NonNull List<? extends AbstractWidget> chatTabButtonsInDisplayOrder) {
        int spacing = 2;
        int rowHeight = 14;
        int leftEdge = getChatLeft();
        int rightEdge = getChatRight();

        int currentX = leftEdge;
        int currentRowY = getChatTopHeight() - spacing - rowHeight;

        // lay out left-to-right; once a row runs out of horizontal space, wrap to a new row directly above it
        for (AbstractWidget chatTabButton : chatTabButtonsInDisplayOrder) {
            int width = chatTabButton.getWidth();

            if (currentX + width > rightEdge && currentX != leftEdge) {
                currentRowY -= (rowHeight + spacing);
                currentX = leftEdge;
            }

            chatTabButton.setPosition(currentX, currentRowY);
            currentX += (width + spacing);
        }
    }

    @Unique
    private static @NonNull String getEffectDurationText(@NonNull MobEffectInstance instance) {
        if (instance.isInfiniteDuration()) {
            return "";
        }

        int totalSeconds = (int) Math.ceil(instance.getDuration() / 20.0);
        if (totalSeconds >= 86400) {
            return (totalSeconds / 86400) + "d";
        } else if (totalSeconds >= 3600) {
            return (totalSeconds / 3600) + "h";
        } else if (totalSeconds > 60) {
            return (totalSeconds / 60) + "m";
        } else {
            return totalSeconds + "s";
        }
    }
}
