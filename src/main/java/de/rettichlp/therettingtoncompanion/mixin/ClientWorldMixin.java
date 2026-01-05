package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.common.configuration.VisualsConfiguration;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.common.configuration.VisualsConfiguration.DayTimeValue.DT_OFF;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Shadow
    @Final
    private ClientWorld.Properties clientWorldProperties;

    @Shadow
    private boolean shouldTickTimeOfDay;

    /**
     * Intercepts the "setTime" method at its head to manipulate the game's time behavior based on the current
     * {@link VisualsConfiguration.DayTimeValue} setting. This method cancels the original time-setting behavior if the
     * {@code DayTimeValue} is not {@code DT_OFF} and updates the game world time accordingly.
     *
     * @param time                The in-game time to set.
     * @param timeOfDay           The current time of day in the game.
     * @param shouldTickTimeOfDay Indicates whether the time of day should progress naturally.
     * @param ci                  The callback information to manage method injection behavior, allowing cancellation of the original
     *                            method.
     */
    @Inject(method = "setTime", at = @At("HEAD"), cancellable = true)
    private void trc$setTimeHead(long time, long timeOfDay, boolean shouldTickTimeOfDay, CallbackInfo ci) {
        VisualsConfiguration.DayTimeValue dayTimeValue = configuration.visuals().getDayTimeValue();
        if (dayTimeValue != DT_OFF) {
            ci.cancel();
            this.clientWorldProperties.setTime(time);
            this.clientWorldProperties.setTimeOfDay(dayTimeValue.getTimeValue());
            this.shouldTickTimeOfDay = false;
        }
    }
}
