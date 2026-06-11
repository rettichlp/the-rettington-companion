package de.rettichlp.therettingtoncompanion.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static net.minecraft.ChatFormatting.AQUA;
import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.LIGHT_PURPLE;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.ChatFormatting.YELLOW;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

@Getter
@AllArgsConstructor
public enum GammaPreset {

    OWN_SETTING(translatable("trc.gamma_preset.own_setting"), LIGHT_PURPLE, -1.0),
    MOODY(translatable("trc.gamma_preset.moody"), RED, 0.0),
    DEFAULT(translatable("trc.gamma_preset.default"), GOLD, 0.5),
    BRIGHT(translatable("trc.gamma_preset.bright"), YELLOW, 1.0),
    FULLBRIGHT_NIGHT_VISION(translatable("trc.gamma_preset.fullbright_night_vision"), AQUA, -1.0),
    FULLBRIGHT_GAMMA(translatable("trc.gamma_preset.fullbright_gamma"), BLUE, 15.0);

    private final Component displayName;
    private final ChatFormatting color;
    private final double gammaValue;

    public GammaPreset next() {
        int nextOrdinal = ordinal() + 1;
        return values()[nextOrdinal % values().length];
    }

    public double getGammaValue() {
        return this.gammaValue >= 0 ? this.gammaValue : configuration.getOwnGammaValue();
    }

    public void sendMessage() {
        MutableComponent component = empty().withStyle(this.color)
                .append(literal("Gamma").withStyle(GRAY))
                .append(literal(": ").withStyle(DARK_GRAY))
                .append(this.displayName);

        switch (this) {
            case OWN_SETTING -> component.append(literal(" (" + toPercent(configuration.getOwnGammaValue()) + "%)"));
            case MOODY, DEFAULT, BRIGHT, FULLBRIGHT_GAMMA -> component.append(literal(" (" + toPercent(this.gammaValue) + "%)"));
            default -> {}
        }

        player.sendOverlayMessage(component);
    }

    private int toPercent(double d) {
        return (int) (d * 100);
    }
}
