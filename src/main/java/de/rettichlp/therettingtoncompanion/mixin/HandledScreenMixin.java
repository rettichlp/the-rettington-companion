package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.screen.slot.SlotActionType.QUICK_MOVE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    protected abstract Slot getSlotAt(double mouseX, double mouseY);

    @Shadow
    protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        Slot slot = getSlotAt(mouseX, mouseY);
        boolean isShiftPressed = this.client.isShiftPressed();
        boolean isMouseLeftDown = (glfwGetMouseButton(this.client.getWindow().getHandle(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS);

        if (slot != null && slot.hasStack() && isShiftPressed && isMouseLeftDown) {
            onMouseClick(slot, slot.id, 0, QUICK_MOVE);
        }
    }
}
