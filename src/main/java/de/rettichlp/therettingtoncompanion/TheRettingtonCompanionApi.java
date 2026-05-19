package de.rettichlp.therettingtoncompanion;

import de.rettichlp.therettingtoncompanion.common.models.Notification;

import java.util.Set;

public interface TheRettingtonCompanionApi {

    Set<Notification> getNotifications();
}
