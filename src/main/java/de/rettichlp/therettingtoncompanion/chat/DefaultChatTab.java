package de.rettichlp.therettingtoncompanion.chat;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static net.minecraft.network.chat.Component.translatable;

public class DefaultChatTab extends AbstractChatTab {

    @Override
    public @NonNull Component getDisplayName() {
        return translatable("trc.chat_screen.chat_tabs.default_tab_name");
    }
}
