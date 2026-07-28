package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.gui.contextualbar.ContextualBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;

@Mixin(ContextualBar.class)
public interface ContextualBarMixin {

    @ModifyArg(method = "extractExperienceLevel",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V",
                        ordinal = 4),
               index = 4)
    private static int trc$extractExperienceLevelInvoke(int color) {
        return configuration.visuals().getExperienceLevelColor();
    }
}
