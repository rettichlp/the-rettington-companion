package de.rettichlp.therettingtoncompanion.common.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minecraft.text.Text;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.common.configuration.VisualsConfiguration.EquipmentModelVisibility.ALL;
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
}
