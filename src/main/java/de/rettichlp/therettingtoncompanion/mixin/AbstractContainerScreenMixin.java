package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.world.inventory.ContainerInput.QUICK_MOVE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (!configuration.inventory().isInstantQuickMove()) {
            return;
        }

        Slot slot = getHoveredSlot(mouseX, mouseY);
        boolean isShiftPressed = this.minecraft.hasShiftDown();
        boolean isMouseLeftDown = (glfwGetMouseButton(this.minecraft.getWindow().handle(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS);

        if (slot != null && slot.hasItem() && isShiftPressed && isMouseLeftDown) {
            onMouseClickAction(slot, QUICK_MOVE);
        }
    }

    @Shadow
    @Nullable
    protected abstract Slot getHoveredSlot(double x, double y);

    @Shadow
    abstract void onMouseClickAction(@Nullable Slot slot, ContainerInput containerInput);
}
