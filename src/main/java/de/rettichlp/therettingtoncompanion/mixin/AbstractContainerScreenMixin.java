package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static net.minecraft.world.inventory.ContainerInput.QUICK_MOVE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

    @Shadow
    @Final
    protected T menu;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "mouseDragged",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;getHoveredSlot(DD)Lnet/minecraft/world/inventory/Slot;",
                     shift = AFTER),
            cancellable = true)
    public void mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy, CallbackInfoReturnable<Boolean> cir) {
        if (!configuration.inventory().isInstantQuickMove()) {
            return;
        }

        Slot slot = getHoveredSlot(event.x(), event.y());

        boolean isShiftPressed = this.minecraft.hasShiftDown();
        boolean isMouseLeftDown = (glfwGetMouseButton(this.minecraft.getWindow().handle(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS);

        MultiPlayerGameMode gameMode = this.minecraft.gameMode;
        if (gameMode != null && slot != null && slot.hasItem() && isShiftPressed && isMouseLeftDown) {
            gameMode.handleContainerInput(this.menu.containerId, slot.index, 0, QUICK_MOVE, player);
            cir.cancel();
        }
    }

    @Shadow
    @Nullable
    protected abstract Slot getHoveredSlot(double x, double y);
}
