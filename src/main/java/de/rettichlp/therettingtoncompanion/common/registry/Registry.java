package de.rettichlp.therettingtoncompanion.common.registry;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;
import static org.atteo.classindex.ClassIndex.getAnnotated;

public class Registry {

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
