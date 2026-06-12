package de.rettichlp.therettingtoncompanion;

import de.rettichlp.therettingtoncompanion.configuration.Configuration;
import de.rettichlp.therettingtoncompanion.services.InventoryService;
import de.rettichlp.therettingtoncompanion.services.NotificationService;
import de.rettichlp.therettingtoncompanion.services.RenderService;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM;
import static net.minecraft.ChatFormatting.AQUA;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;

public class TheRettingtonCompanion implements ModInitializer {

    public static final String MOD_ID = "the-rettington-companion";
    public static final String MOD_NAME = "The Rettington Companion";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final InventoryService inventoryService = new InventoryService();
    public static final NotificationService notificationService = new NotificationService();
    public static final RenderService renderService = new RenderService();

    public static final Configuration configuration = new Configuration().loadFromFile();

    public static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(MOD_ID, "trc.key.category.name"));
    public static final KeyMapping GAMMA_PRESET_KEY = new KeyMapping("trc.key.gamma_preset", KEYSYM, GLFW_KEY_G, KEY_CATEGORY);
    public static final KeyMapping EQUIPMENT_MODEL_VISIBILITY_KEY = new KeyMapping("trc.key.hide_armor", KEYSYM, GLFW_KEY_H, KEY_CATEGORY);
    public static final KeyMapping SCREENSHOT_KEY = new KeyMapping("trc.key.screenshot", KEYSYM, GLFW_KEY_F12, KEY_CATEGORY);

    public static LocalPlayer player;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            player = client.player;

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
    private void sendWorldInfoOnJoin(@NotNull Minecraft client) {
        String worldName = "?";

        boolean isMultiplayerServer = !client.isLocalServer() || client.getSingleplayerServer() != null && client.getSingleplayerServer().isPublished();

        if (!isMultiplayerServer) {
            assert client.getSingleplayerServer() != null;
            worldName = client.getSingleplayerServer().getWorldData().getLevelName();
        } else if (client.getCurrentServer() != null) {
            worldName = client.getCurrentServer().name.isBlank() ? client.getCurrentServer().ip : client.getCurrentServer().name;
        }

        player.sendSystemMessage(empty()
                .append(literal("[").withStyle(GRAY))
                .append(literal(worldName).withStyle(AQUA))
                .append(literal("]").withStyle(GRAY)));
    }
}
