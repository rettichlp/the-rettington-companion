package de.rettichlp.therettingtoncompanion.chat;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static net.minecraft.network.chat.Component.literal;

public class AddChatTab extends AbstractChatTab {

    @Override
    public @NonNull Component getDisplayName() {
        return literal("+");
    }
}
