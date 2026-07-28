package de.rettichlp.therettingtoncompanion.models;

import de.rettichlp.therettingtoncompanion.gui.ICycleButtonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.TextColor.AQUA;
import static net.minecraft.network.chat.TextColor.BLUE;
import static net.minecraft.network.chat.TextColor.DARK_GRAY;
import static net.minecraft.network.chat.TextColor.GOLD;
import static net.minecraft.network.chat.TextColor.GRAY;
import static net.minecraft.network.chat.TextColor.LIGHT_PURPLE;
import static net.minecraft.network.chat.TextColor.RED;
import static net.minecraft.network.chat.TextColor.YELLOW;

@Getter
@AllArgsConstructor
public enum GammaPreset implements ICycleButtonValue {

    OWN_SETTING(translatable("trc.gamma_preset.own_setting"), LIGHT_PURPLE, 0.5),
    MOODY(translatable("trc.gamma_preset.moody"), RED, 0.0),
    DEFAULT(translatable("trc.gamma_preset.default"), GOLD, 0.5),
    BRIGHT(translatable("trc.gamma_preset.bright"), YELLOW, 1.0),
    FULLBRIGHT_NIGHT_VISION(translatable("trc.gamma_preset.fullbright_night_vision"), AQUA, 0.5),
    FULLBRIGHT_GAMMA(translatable("trc.gamma_preset.fullbright_gamma"), BLUE, 15.0);

    private final MutableComponent value;
    private final TextColor color;
    private final double gammaValue;

    @Override
    public @NonNull Component value() {
        return this.value.withColor(this.color);
    }

    @Contract(" -> new")
    @Override
    public @NonNull Tooltip tooltip() {
        return create(empty());
    }

    public GammaPreset next() {
        int nextOrdinal = ordinal() + 1;
        return values()[nextOrdinal % values().length];
    }

    public void sendMessage() {
        MutableComponent component = empty().withColor(this.color)
                .append(literal("Gamma").withColor(GRAY))
                .append(literal(": ").withColor(DARK_GRAY))
                .append(this.value);

        switch (this) {
            case OWN_SETTING -> component.append(literal(" (" + toPercent(Minecraft.getInstance().options.gamma().get()) + "%)"));
            case MOODY, DEFAULT, BRIGHT, FULLBRIGHT_GAMMA -> component.append(literal(" (" + toPercent(this.gammaValue) + "%)"));
            default -> {
            }
        }

        player.sendOverlayMessage(component);
    }

    private int toPercent(double d) {
        return (int) (d * 100);
    }
}
