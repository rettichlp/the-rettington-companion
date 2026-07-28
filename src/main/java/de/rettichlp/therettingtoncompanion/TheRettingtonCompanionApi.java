package de.rettichlp.therettingtoncompanion;

import de.rettichlp.therettingtoncompanion.gui.options.list.HiddenMessageEntry;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget;
import de.rettichlp.therettingtoncompanion.models.Notification;

import java.util.List;
import java.util.Set;

public interface TheRettingtonCompanionApi {

    Set<Notification> getNotifications();

    List<AbstractTRCWidget<?>> getWidgets();

    Set<HiddenMessageEntry.HiddenMessage> getHiddenMessages();
}
