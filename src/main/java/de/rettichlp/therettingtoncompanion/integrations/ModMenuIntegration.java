package de.rettichlp.therettingtoncompanion.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.rettichlp.therettingtoncompanion.gui.options.TRCOptionsScreen;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new TRCOptionsScreen("general", parent, true);
    }
}
