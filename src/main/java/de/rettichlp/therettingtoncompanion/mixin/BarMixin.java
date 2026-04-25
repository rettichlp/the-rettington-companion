package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.gui.hud.bar.Bar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;

@Mixin(Bar.class)
public interface BarMixin {

    @ModifyArg(method = "drawExperienceLevel",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V",
                        ordinal = 4),
               index = 4)
    private static int trc$drawExperienceLevelInvoke(int color) {
        return configuration.visuals().getExperienceLevelColor();
    }
}
