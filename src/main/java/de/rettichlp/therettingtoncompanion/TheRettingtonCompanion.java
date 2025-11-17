package de.rettichlp.therettingtoncompanion;

import de.rettichlp.therettingtoncompanion.common.Configuration;
import de.rettichlp.therettingtoncompanion.common.registry.Registry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheRettingtonCompanion implements ModInitializer {

    public static final String MOD_ID = "the-rettington-companion";
    public static final String MOD_NAME = "The Rettington Companion";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Configuration configuration = new Configuration().loadFromFile();

    public static ClientPlayerEntity player;
    public static ClientPlayNetworkHandler networkHandler;

    private final Registry registry = new Registry();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            player = client.player;
            networkHandler = handler;

            client.execute(this.registry::registerListeners);
        });
    }
}
