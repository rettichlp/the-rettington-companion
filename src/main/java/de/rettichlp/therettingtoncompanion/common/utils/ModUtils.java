package de.rettichlp.therettingtoncompanion.common.utils;

import net.fabricmc.loader.api.FabricLoader;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;

public class ModUtils {

    public static String getVersionString() {
        return FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString())
                .orElseThrow(() -> new NullPointerException("Cannot find version"));
    }
}
