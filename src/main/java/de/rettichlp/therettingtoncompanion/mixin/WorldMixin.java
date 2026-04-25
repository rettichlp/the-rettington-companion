package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.common.configuration.VisualsConfiguration;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;

@Mixin(World.class)
public abstract class WorldMixin {

    @Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true)
    public void trc$getRainGradient(float delta, CallbackInfoReturnable<Float> callback) {
        VisualsConfiguration.WeatherValue weatherValue = configuration.visuals().getWeatherValue();

        switch (weatherValue) {
            case W_CLEAR -> callback.setReturnValue(0F);
            case W_RAIN, W_THUNDER -> callback.setReturnValue(1F);
        }
    }

    @Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
    public void trc$getThunderGradient(float delta, CallbackInfoReturnable<Float> callback) {
        VisualsConfiguration.WeatherValue weatherValue = configuration.visuals().getWeatherValue();

        switch (weatherValue) {
            case W_CLEAR, W_RAIN -> callback.setReturnValue(0F);
            case W_THUNDER -> callback.setReturnValue(1F);
        }
    }
}
