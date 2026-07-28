package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration;
import de.rettichlp.therettingtoncompanion.models.GammaPreset;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

import static com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.notificationService;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.utils.ModUtils.delayedAction;
import static de.rettichlp.therettingtoncompanion.utils.ScreenshotUtils.takeImgurScreenshot;
import static de.rettichlp.therettingtoncompanion.utils.ScreenshotUtils.uploadImageToImgur;
import static java.awt.Color.CYAN;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static xaero.common.effect.Effects.NO_MINIMAP;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

    @Unique
    private static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(fromNamespaceAndPath(MOD_ID, "trc.key.category.name"));

    @Unique
    private static final KeyMapping GAMMA_PRESET_KEY = new KeyMapping("trc.key.gamma_preset", KEYSYM, GLFW_KEY_G, KEY_CATEGORY);

    @Unique
    private static final KeyMapping EQUIPMENT_MODEL_VISIBILITY_KEY = new KeyMapping("trc.key.hide_armor", KEYSYM, GLFW_KEY_H, KEY_CATEGORY);

    @Unique
    private static final KeyMapping SCREENSHOT_KEY = new KeyMapping("trc.key.screenshot", KEYSYM, GLFW_KEY_F12, KEY_CATEGORY);

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/KeyMapping;matches(Lnet/minecraft/client/input/KeyEvent;)Z",
                     ordinal = 0))
    private void trc$keyPressInvoke(long handle, int action, KeyEvent event, CallbackInfo ci) {
        // support focused chat
        if (SCREENSHOT_KEY.matches(event)) {
            player.addEffect(new MobEffectInstance(NO_MINIMAP, -1, 0, false, false, false));

            delayedAction(() -> takeImgurScreenshot().thenAccept(file -> {
                CompletableFuture<String> futureImageLink = uploadImageToImgur(file.toPath());
                futureImageLink.thenAccept(link -> {
                    notificationService.sendNotification(translatable("trc.notification.screenshot_uploaded"), CYAN, 5000);
                    this.minecraft.keyboardHandler.setClipboard(link);
                });
            }), 100);

            delayedAction(() -> player.removeEffect(NO_MINIMAP), 1000);
        }

        // only with closed chat
        if (!this.minecraft.gui.hud.getChat().isChatFocused()) {
            if (EQUIPMENT_MODEL_VISIBILITY_KEY.matches(event)) {
                VisualsConfiguration.EquipmentModelVisibility equipmentModelVisibility = configuration.visuals().getEquipmentModelVisibility().next();
                configuration.visuals().setEquipmentModelVisibility(equipmentModelVisibility);
                equipmentModelVisibility.sendMessage();
            }

            if (GAMMA_PRESET_KEY.matches(event)) {
                GammaPreset newGammaPreset = configuration.visuals().getGammaPreset().next();
                configuration.visuals().setGammaPreset(newGammaPreset);
                newGammaPreset.sendMessage();
            }
        }
    }
}
