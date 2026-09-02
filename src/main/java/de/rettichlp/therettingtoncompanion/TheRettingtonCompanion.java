package de.rettichlp.therettingtoncompanion;

import de.rettichlp.therettingtoncompanion.configuration.Configuration;
import de.rettichlp.therettingtoncompanion.services.ChatLogService;
import de.rettichlp.therettingtoncompanion.services.InventoryService;
import de.rettichlp.therettingtoncompanion.services.NotificationService;
import de.rettichlp.therettingtoncompanion.services.VisualsService;
import de.rettichlp.therettingtoncompanion.services.WidgetService;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import static com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM;
import static net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STOPPING;
import static net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping;
import static net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.JOIN;
import static net.minecraft.client.KeyMapping.Category.register;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.TextColor.AQUA;
import static net.minecraft.network.chat.TextColor.GRAY;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;
import static org.slf4j.LoggerFactory.getLogger;

public class TheRettingtonCompanion implements ModInitializer {

    public static final String MOD_ID = "the-rettington-companion";
    public static final String MOD_NAME = "The Rettington Companion";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = getLogger(MOD_ID);

    public static final Configuration configuration = new Configuration().loadFromFile();

    public static final ChatLogService chatLogService = new ChatLogService();
    public static final InventoryService inventoryService = new InventoryService();
    public static final NotificationService notificationService = new NotificationService();
    public static final VisualsService visualsService = new VisualsService();
    public static final WidgetService widgetService = new WidgetService();

    public static final KeyMapping.Category KEY_CATEGORY = register(fromNamespaceAndPath(MOD_ID, "name"));
    public static final KeyMapping CHAT_PEEK_KEY = registerKeyMapping(new KeyMapping("trc.key.chat_peek", KEYSYM, GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    public static final KeyMapping GAMMA_PRESET_KEY = registerKeyMapping(new KeyMapping("trc.key.gamma_preset", KEYSYM, GLFW_KEY_G, KEY_CATEGORY));
    public static final KeyMapping EQUIPMENT_MODEL_VISIBILITY_KEY = registerKeyMapping(new KeyMapping("trc.key.hide_armor", KEYSYM, GLFW_KEY_H, KEY_CATEGORY));
    public static final KeyMapping SCREENSHOT_KEY = registerKeyMapping(new KeyMapping("trc.key.screenshot", KEYSYM, GLFW_KEY_F12, KEY_CATEGORY));

    public static LocalPlayer player;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        widgetService.initWidgets();

        JOIN.register((_, _, minecraft) -> {
            player = minecraft.player;

            if (configuration.chat().isSaveChatLog()) {
                chatLogService.loadChatLogIfNeeded();
            }

            if (configuration.chat().isKeepMessagesOnDisconnect()) {
                sendWorldInfoOnJoin(minecraft);
            }
        });

        CLIENT_STOPPING.register(_ -> {
            if (configuration.chat().isSaveChatLog()) {
                chatLogService.saveChatLog();
            }
        });
    }

    /**
     * Sends the world information (server name or save file name) to the player when they join a server or single-player world.
     * Displays the world information as a chat message formatted with specific colors.
     *
     * @param minecraft The Minecraft client instance. Used to determine whether the player is in a single-player world or a server,
     *                  and to retrieve the necessary information for the current world.
     */
    private void sendWorldInfoOnJoin(@NonNull Minecraft minecraft) {
        String worldName = "?";

        if (minecraft.isLocalServer() && minecraft.getSingleplayerServer() != null && !minecraft.getSingleplayerServer().isPublished()) {
            worldName = minecraft.getSingleplayerServer().getWorldData().getLevelName();
        } else if (minecraft.getCurrentServer() != null) {
            worldName = minecraft.getCurrentServer().name.isBlank() ? minecraft.getCurrentServer().ip : minecraft.getCurrentServer().name;
        }

        player.sendSystemMessage(empty()
                .append(literal("[").withColor(GRAY))
                .append(literal(worldName).withColor(AQUA))
                .append(literal("]").withColor(GRAY)));
    }
}
