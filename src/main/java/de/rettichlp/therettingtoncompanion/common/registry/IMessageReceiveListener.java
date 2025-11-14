package de.rettichlp.therettingtoncompanion.common.registry;

import net.minecraft.text.Text;

public interface IMessageReceiveListener extends IListener {

    boolean onMessageReceive(Text text, String message);
}
