package de.rettichlp.therettingtoncompanion.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.CommonComponents.OPTION_OFF;
import static net.minecraft.network.chat.CommonComponents.OPTION_ON;
import static net.minecraft.network.chat.Component.empty;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum OnOffCycleButtonEntry implements ICycleButtonValue {

    ON(OPTION_ON, create(empty())),
    OFF(OPTION_OFF, create(empty()));

    private final Component value;
    private final Tooltip tooltip;
}
