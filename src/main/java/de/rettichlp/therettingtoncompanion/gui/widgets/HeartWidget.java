package de.rettichlp.therettingtoncompanion.gui.widgets;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCTextWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.TextColor.DARK_RED;
import static net.minecraft.network.chat.TextColor.GOLD;
import static net.minecraft.network.chat.TextColor.RED;
import static net.minecraft.network.chat.TextColor.WHITE;

public class HeartWidget extends AbstractTRCTextWidget<HeartWidget.Configuration> {

    @Override
    public @Nullable String getRegistryName() {
        return "heart";
    }

    @Override
    public Component getLabel() {
        return translatable("trc.widgets.heart.label");
    }

    @Override
    public Component getTooltip() {
        return translatable("trc.widgets.heart.tooltip");
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

        boolean showWarning = overallAmount < 15 && (currentTimeMillis() / 200 % 2 == 0);

        TextColor textColor;
        if (absorptionAmount > 0) {
            textColor = GOLD;
        } else if (showWarning) {
            textColor = DARK_RED;
        } else if (overallAmount <= 20) {
            textColor = RED;
        } else {
            textColor = WHITE;
        }

        MutableComponent text = literal(overallAmountString).withColor(textColor);
        return text.append(literal(" ❤").withColor(DARK_RED));
    }

    public static class Configuration extends WidgetConfiguration {}
}
