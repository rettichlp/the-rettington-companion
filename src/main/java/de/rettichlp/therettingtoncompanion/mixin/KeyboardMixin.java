package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.common.configuration.VisualsConfiguration;
import de.rettichlp.therettingtoncompanion.common.models.GammaPreset;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding;
import static net.minecraft.client.util.InputUtil.Type.KEYSYM;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {

    @Unique
    private static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(Identifier.of(MOD_ID, "trc.key.category.name"));
    @Unique
    private static final KeyBinding GAMMA_PRESET_KEY = registerKeyBinding(new KeyBinding("trc.key.gamma_preset", KEYSYM, GLFW_KEY_G, KEY_CATEGORY));
    @Unique
    private static final KeyBinding EQUIPMENT_MODEL_VISIBILITY_KEY = registerKeyBinding(new KeyBinding("trc.key.hide_armor", KEYSYM, GLFW_KEY_H, KEY_CATEGORY));

    @Inject(method = "onKey", at = @At(value = "INVOKE",
                                       target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(Lnet/minecraft/client/input/KeyInput;)Z",
                                       ordinal = 0))
    private void trc$onKeyInvoke(long window, int action, KeyInput input, CallbackInfo ci) {
        if (EQUIPMENT_MODEL_VISIBILITY_KEY.wasPressed()) {
            VisualsConfiguration.EquipmentModelVisibility equipmentModelVisibility = configuration.visuals().getEquipmentModelVisibility().next();
            configuration.visuals().setEquipmentModelVisibility(equipmentModelVisibility);
            equipmentModelVisibility.sendMessage();
        }

        if (GAMMA_PRESET_KEY.wasPressed()) {
            GammaPreset newGammaPreset = configuration.getGammaPreset().next();
            configuration.setGammaPreset(newGammaPreset);
            newGammaPreset.sendMessage();
        }
    }
}
