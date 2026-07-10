package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.DayTimeValue.DT_OFF;

@Mixin(Level.class)
public class LevelMixin {

    @Inject(method = "getThunderLevel", at = @At("HEAD"), cancellable = true)
    public void trc$getThunderLevelHead(float a, CallbackInfoReturnable<Float> callback) {
        VisualsConfiguration.WeatherValue weatherValue = configuration.visuals().getWeatherValue();

        switch (weatherValue) {
            case W_CLEAR, W_RAIN -> callback.setReturnValue(0F);
            case W_THUNDER -> callback.setReturnValue(1F);
        }
    }

    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
    public void trc$getRainLevelHead(float a, CallbackInfoReturnable<Float> callback) {
        VisualsConfiguration.WeatherValue weatherValue = configuration.visuals().getWeatherValue();

        switch (weatherValue) {
            case W_CLEAR -> callback.setReturnValue(0F);
            case W_RAIN, W_THUNDER -> callback.setReturnValue(1F);
        }
    }

    @Inject(method = "getClockTimeTicks", at = @At("HEAD"), cancellable = true)
    private void trc$getClockTimeTicksHead(Optional<? extends Holder<WorldClock>> clock, CallbackInfoReturnable<Long> cir) {
        VisualsConfiguration.DayTimeValue dayTimeValue = configuration.visuals().getDayTimeValue();
        if (dayTimeValue != DT_OFF) {
            cir.setReturnValue((long) dayTimeValue.getTimeValue());
        }
    }
}
