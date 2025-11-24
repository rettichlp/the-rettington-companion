package de.rettichlp.therettingtoncompanion.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.AQUA;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.LIGHT_PURPLE;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

@Getter
@AllArgsConstructor
public enum GammaPreset {

    OWN_SETTING(translatable("trc.gamma_preset.own_setting"), LIGHT_PURPLE, -1.0),
    MOODY(translatable("trc.gamma_preset.moody"), RED, 0.0),
    DEFAULT(translatable("trc.gamma_preset.default"), GOLD, 0.5),
    BRIGHT(translatable("trc.gamma_preset.bright"), YELLOW, 1.0),
    FULLBRIGHT(translatable("trc.gamma_preset.fullbright"), AQUA, 15.0);

    private final Text displayName;
    private final Formatting color;
    private final double gammaValue;

    public GammaPreset next() {
        int nextOrdinal = ordinal() + 1;
        return values()[nextOrdinal % values().length];
    }

    public double getGammaValue() {
        return this.gammaValue >= 0 ? this.gammaValue : configuration.getOwnGammaValue();
    }

    public void sendMessage() {
        player.sendMessage(empty().formatted(this.color)
                .append(literal("Gamma").formatted(GRAY))
                .append(literal(": ").formatted(DARK_GRAY))
                .append(this.displayName.copy())
                .append(literal(" (" + toPercent() + "%)")), true);
    }

    private int toPercent() {
        return (int) (getGammaValue() * 100);
    }
}
