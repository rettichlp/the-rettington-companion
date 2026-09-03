package de.rettichlp.therettingtoncompanion.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.network.chat.Component;

@Data
@AllArgsConstructor
public class ChatLogEntry {

    private Component content;
    private GuiMessageSource source;
    private long timestamp;
}
