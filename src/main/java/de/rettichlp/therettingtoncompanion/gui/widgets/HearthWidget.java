package de.rettichlp.therettingtoncompanion.gui.widgets;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCTextWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static java.lang.String.format;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.ChatFormatting.YELLOW;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

public class HearthWidget extends AbstractTRCTextWidget<HearthWidget.Configuration> {

    @Override
    public @Nullable String getRegistryName() {
        return "hearth";
    }

    @Override
    public Component getLabel() {
        return translatable("trc.widgets.hearth.label");
    }

    @Override
    public Component getTooltip() {
        return translatable("trc.widgets.hearth.tooltip");
    }

    @Override
    public void addOptions(@NonNull TRCOptionsList optionsList) {}

    @Override
    public Component text() {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null; // cannot be null at this point

        float absorptionAmount = player.getAbsorptionAmount();
        float overallAmount = player.getHealth() + absorptionAmount;

        String overallAmountString = format("%.1f", overallAmount / 2).replaceAll(",0$", "");
        MutableComponent text = literal(overallAmountString).withStyle(absorptionAmount > 0 ? YELLOW : GRAY);
        return text.append(literal("❤").withStyle(RED));
    }

    public static class Configuration extends WidgetConfiguration {}
}
