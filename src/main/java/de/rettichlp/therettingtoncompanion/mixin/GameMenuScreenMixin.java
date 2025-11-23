package de.rettichlp.therettingtoncompanion.mixin;

import de.rettichlp.therettingtoncompanion.common.gui.screens.components.IconButtonWidget;
import de.rettichlp.therettingtoncompanion.common.gui.screens.options.ModOptionScreen;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    @Unique
    private ButtonWidget buttonWidget;

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.buttonWidget != null && this.buttonWidget.mouseClicked(click, doubled)) {
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void addOptionsButton(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        this.buttonWidget = new IconButtonWidget(0, 0, Identifier.of(MOD_ID, "icon.png"), 0, 0, 12, 12, button -> this.client.setScreen(new ModOptionScreen()));

        Window window = this.client.getWindow();
        this.buttonWidget.setPosition(window.getScaledWidth() / 2 + 102 + 8, window.getScaledHeight() / 4 + 8);

        this.buttonWidget.render(context, mouseX, mouseY, deltaTicks);
    }
}
