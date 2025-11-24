package de.rettichlp.therettingtoncompanion.common.registry;

import de.rettichlp.therettingtoncompanion.common.models.GammaPreset;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.common.models.GammaPreset.OWN_SETTING;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;
import static net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding;
import static net.minecraft.client.util.InputUtil.Type.KEYSYM;
import static org.atteo.classindex.ClassIndex.getAnnotated;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;

public class Registry {

    public static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(Identifier.of(MOD_ID, "trc.key.category.name"));
    public static final KeyBinding GAMMA_PRESET_KEY = registerKeyBinding(new KeyBinding("trc.key.gamma_preset", KEYSYM, GLFW_KEY_G, KEY_CATEGORY));
    public static final KeyBinding HIDE_ARMOR_KEY = registerKeyBinding(new KeyBinding("trc.key.hide_armor", KEYSYM, GLFW_KEY_H, KEY_CATEGORY));

    private final Set<IListener> listenerInstances = getListenerInstances();

    private boolean initialized = false;

    public void registerListeners() {
        if (this.initialized) {
            LOGGER.warn("Listeners already registered");
            return;
        }

        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            String rawMessage = message.getString();

            // handle message receiving
            boolean showMessage = getListenersImplementing(IMessageReceiveListener.class).stream()
                    .allMatch(iMessageReceiveListener -> iMessageReceiveListener.onMessageReceive(message, rawMessage));

            if (!showMessage) {
                LOGGER.info("Message hidden: {}", message.getString());
            }

            return showMessage;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client == null) {
                return;
            }

            if (HIDE_ARMOR_KEY.wasPressed()) {
                configuration.setHideArmor(!configuration.isHideArmor());
            }

            if (GAMMA_PRESET_KEY.wasPressed()) {
                GammaPreset newGammaPreset = ofNullable(configuration.getGammaPreset()).orElse(OWN_SETTING).next();
                configuration.setGammaPreset(newGammaPreset);
                newGammaPreset.sendMessage();
            }
        });

        // prevent multiple registrations of listeners
        this.initialized = true;
    }

    private @NotNull Set<IListener> getListenerInstances() {
        return stream(getAnnotated(Listener.class).spliterator(), false)
                .map(listenerClass -> {
                    try {
                        return (IListener) listenerClass.getConstructor().newInstance();
                    } catch (Exception e) {
                        LOGGER.error("Error while registering listener: {}", listenerClass.getName(), e.getCause());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(toSet());
    }

    private <T> Set<T> getListenersImplementing(@NotNull Class<T> listenerInterface) {
        return this.listenerInstances.stream()
                .filter(listenerInterface::isInstance)
                .map(listenerInterface::cast)
                .collect(toSet());
    }
}
