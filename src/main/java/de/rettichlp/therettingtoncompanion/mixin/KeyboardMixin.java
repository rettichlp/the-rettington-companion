package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.common.configuration.VisualsConfiguration;
import de.rettichlp.therettingtoncompanion.common.models.GammaPreset;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.EQUIPMENT_MODEL_VISIBILITY_KEY;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.GAMMA_PRESET_KEY;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.SCREENSHOT_KEY;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.notificationService;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.common.utils.ModUtils.delayedAction;
import static de.rettichlp.therettingtoncompanion.common.utils.ScreenshotUtils.takeScreenshot;
import static de.rettichlp.therettingtoncompanion.common.utils.ScreenshotUtils.uploadImageToImgur;
import static java.awt.Color.CYAN;
import static net.minecraft.text.Text.translatable;
import static xaero.common.effect.Effects.NO_MINIMAP;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onKey", at = @At(value = "INVOKE",
                                       target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(Lnet/minecraft/client/input/KeyInput;)Z",
                                       ordinal = 0))
    private void trc$onKeyInvoke(long window, int action, KeyInput input, CallbackInfo ci) {
        // support focused chat
        if (SCREENSHOT_KEY.matchesKey(input)) {
            player.addStatusEffect(new StatusEffectInstance(NO_MINIMAP, -1, 0, false, false, false));

            delayedAction(() -> takeScreenshot().thenAccept(file -> {
                CompletableFuture<String> futureImageLink = uploadImageToImgur(file.toPath());
                futureImageLink.thenAccept(link -> {
                    notificationService.sendNotification(translatable("trc.notification.screenshot_uploaded"), CYAN, 5000);
                    this.client.keyboard.setClipboard(link);
                });
            }), 100);

            delayedAction(() -> player.removeStatusEffect(NO_MINIMAP), 1000);
        }

        // only with closed chat
        if (!this.client.inGameHud.getChatHud().isChatFocused()) {
            if (EQUIPMENT_MODEL_VISIBILITY_KEY.matchesKey(input)) {
                VisualsConfiguration.EquipmentModelVisibility equipmentModelVisibility = configuration.visuals().getEquipmentModelVisibility().next();
                configuration.visuals().setEquipmentModelVisibility(equipmentModelVisibility);
                equipmentModelVisibility.sendMessage();
            }

            if (GAMMA_PRESET_KEY.matchesKey(input)) {
                GammaPreset newGammaPreset = configuration.getGammaPreset().next();
                configuration.setGammaPreset(newGammaPreset);
                newGammaPreset.sendMessage();
            }
        }
    }
}
