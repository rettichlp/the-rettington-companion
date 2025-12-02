package de.rettichlp.therettingtoncompanion;

import de.rettichlp.therettingtoncompanion.common.configuration.Configuration;
import de.rettichlp.therettingtoncompanion.common.registry.Registry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
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

    private final Registry registry = new Registry();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            player = client.player;

            client.execute(this.registry::registerListeners);

            if (configuration.chat().isKeepMessagesOnDisconnect()) {
                sendWorldInfoOnJoin(client);
            }
        });
    }

    /**
     * Sends the world information (server name or save file name) to the player when they join a server or single-player world.
     * Displays the world information as a chat message formatted with specific colors.
     *
     * @param client The Minecraft client instance. Used to determine whether the player is in a single-player world or a server, and
     *               to retrieve the necessary information for the current world.
     */
    private void sendWorldInfoOnJoin(@NotNull MinecraftClient client) {
        String worldName = "?";
        if (client.isIntegratedServerRunning()) {
            assert client.getServer() != null;
            worldName = client.getServer().getSaveProperties().getLevelName();
        } else if (client.getCurrentServerEntry() instanceof ServerInfo serverInfo) {
            worldName = serverInfo.name.isBlank() ? serverInfo.address : serverInfo.name;
        }

        player.sendMessage(Text.empty()
                .append(Text.literal("[").formatted(Formatting.GRAY))
                .append(Text.literal(worldName).formatted(Formatting.AQUA))
                .append(Text.literal("]").formatted(Formatting.GRAY)), false);
    }
}
