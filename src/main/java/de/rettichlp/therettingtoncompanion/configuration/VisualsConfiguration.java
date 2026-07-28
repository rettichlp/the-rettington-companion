package de.rettichlp.therettingtoncompanion.configuration;

import de.rettichlp.therettingtoncompanion.gui.ICycleButtonValue;
import de.rettichlp.therettingtoncompanion.models.GammaPreset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.DayTimeValue.DT_OFF;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.EquipmentModelVisibility.ALL;
import static de.rettichlp.therettingtoncompanion.configuration.VisualsConfiguration.WeatherValue.W_OFF;
import static de.rettichlp.therettingtoncompanion.models.GammaPreset.OWN_SETTING;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.CommonComponents.OPTION_OFF;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.TextColor.DARK_GRAY;
import static net.minecraft.network.chat.TextColor.GRAY;

@Data
public class VisualsConfiguration {

    private boolean showArmorHud = true;
    private boolean showArrowHud = true;
    private EquipmentModelVisibility equipmentModelVisibility = ALL;
    private GammaPreset gammaPreset = OWN_SETTING;
    private DayTimeValue dayTimeValue = DT_OFF;
    private WeatherValue weatherValue = W_OFF;
    private int experienceLevelColor = -8323296;
    private boolean showEmptyInventorySlotCount = false;

    @Getter
    @Accessors(fluent = true)
    @AllArgsConstructor
    public enum EquipmentModelVisibility implements ICycleButtonValue {

        ALL(translatable("trc.equipment_model_visibility.all")),
        NONE(translatable("trc.equipment_model_visibility.none")),
        ONLY_WINGS(translatable("trc.equipment_model_visibility.only_wings"));

        private final Component value;

        @Contract(" -> new")
        @Override
        public @NonNull Tooltip tooltip() {
            return create(empty());
        }

        public EquipmentModelVisibility next() {
            int nextOrdinal = ordinal() + 1;
            return values()[nextOrdinal % values().length];
        }

        public void sendMessage() {
            player.sendOverlayMessage(empty()
                    .append(literal("Equipment").withColor(GRAY))
                    .append(literal(": ").withColor(DARK_GRAY))
                    .append(this.value));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum DayTimeValue implements ICycleButtonValue {

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

        @Override
        public Component value() {
            return this == DT_OFF ? OPTION_OFF : literal(String.valueOf(this.timeValue));
        }

        @Contract(" -> new")
        @Override
        public @NonNull Tooltip tooltip() {
            return create(empty());
        }
    }

    @Getter
    @Accessors(fluent = true)
    @AllArgsConstructor
    public enum WeatherValue implements ICycleButtonValue {

        W_OFF(OPTION_OFF, create(empty())),
        W_CLEAR(translatable("trc.weather_value.clear"), create(empty())),
        W_RAIN(translatable("trc.weather_value.rain"), create(empty())),
        W_THUNDER(translatable("trc.weather_value.thunder"), create(empty()));

        private final Component value;
        private final Tooltip tooltip;
    }
}
