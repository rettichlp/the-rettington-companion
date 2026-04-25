package de.rettichlp.therettingtoncompanion.common.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minecraft.text.Text;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.common.configuration.VisualsConfiguration.DayTimeValue.DT_OFF;
import static de.rettichlp.therettingtoncompanion.common.configuration.VisualsConfiguration.EquipmentModelVisibility.ALL;
import static de.rettichlp.therettingtoncompanion.common.configuration.VisualsConfiguration.WeatherValue.W_OFF;
import static net.minecraft.screen.ScreenTexts.OFF;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.GRAY;

@Data
public class VisualsConfiguration {

    private boolean showArmorHud = true;
    private boolean showArrowHud = true;
    private EquipmentModelVisibility equipmentModelVisibility = ALL;
    private DayTimeValue dayTimeValue = DT_OFF;
    private WeatherValue weatherValue = W_OFF;

    @Getter
    @AllArgsConstructor
    public enum EquipmentModelVisibility {

        ALL(translatable("trc.equipment_model_visibility.all")),
        NONE(translatable("trc.equipment_model_visibility.none")),
        ONLY_WINGS(translatable("trc.equipment_model_visibility.only_wings"));

        private final Text displayName;

        public EquipmentModelVisibility next() {
            int nextOrdinal = ordinal() + 1;
            return values()[nextOrdinal % values().length];
        }

        public void sendMessage() {
            player.sendMessage(empty()
                    .append(literal("Equipment").formatted(GRAY))
                    .append(literal(": ").formatted(DARK_GRAY))
                    .append(this.displayName), true);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum DayTimeValue {

        DT_OFF(-1),
        DT_0(0),
        DT_3000(3000),
        DT_6000(6000),
        DT_9000(9000),
        DT_12000(12000),
        DT_15000(15000),
        DT_18000(18000),
        DT_21000(21000);

        private final int timeValue;

        public Text getDisplayName() {
            return this == DT_OFF ? OFF : literal(String.valueOf(this.timeValue));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum WeatherValue {

        W_OFF(OFF),
        W_CLEAR(translatable("trc.weather_value.clear")),
        W_RAIN(translatable("trc.weather_value.rain")),
        W_THUNDER(translatable("trc.weather_value.thunder"));

        private final Text displayName;
    }
}
