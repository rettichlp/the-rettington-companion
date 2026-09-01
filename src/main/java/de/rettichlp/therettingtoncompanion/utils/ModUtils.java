package de.rettichlp.therettingtoncompanion.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static com.mojang.serialization.JsonOps.INSTANCE;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static java.util.regex.Pattern.compile;
import static net.minecraft.network.chat.ComponentSerialization.CODEC;

public class ModUtils {

    public static final Gson GSON = gsonBuilder().setPrettyPrinting().create();

    public static final Gson GSON_COMPACT = gsonBuilder().create(); // no pretty printing, keeps large data sets in a single line

    private static final Pattern IPV4_PATTERN = compile("^\\d{1,3}(\\.\\d{1,3}){3}$");

    public static @NonNull String getVersionString() {
        return FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString())
                .orElseThrow(() -> new NullPointerException("Cannot find version"));
    }

    public static void delayedAction(Runnable runnable, long milliseconds) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Minecraft.getInstance().execute(runnable);
            }
        }, milliseconds);
    }

    /**
     * The base domain (subdomain stripped) of the server the player is currently connected to, or {@code null} if not on a multiplayer
     * server.
     */
    public static @Nullable String getCurrentServerBaseDomain() {
        var currentServer = Minecraft.getInstance().getCurrentServer();
        return currentServer == null ? null : getBaseDomain(currentServer.ip);
    }

    public static @Nullable String getBaseDomain(@Nullable String host) {
        if (host == null || host.isBlank()) {
            return null;
        }

        String hostname = host.split(":")[0].toLowerCase();
        if (IPV4_PATTERN.matcher(hostname).matches()) {
            return hostname; // IP addresses have no meaningful "base domain" to strip a subdomain from
        }

        String[] labels = hostname.split("\\.");
        return labels.length < 2 ? hostname : (labels[labels.length - 2] + "." + labels[labels.length - 1]);
    }

    private static @NonNull GsonBuilder gsonBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, typeOfT, context) -> Instant.parse(json.getAsString()))
                .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) -> LocalTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(Pattern.class, (JsonDeserializer<Pattern>) (json, typeOfT, context) -> Pattern.compile(json.getAsString()))
                .registerTypeAdapter(Pattern.class, (JsonSerializer<Pattern>) (src, typeOfSrc, context) -> new JsonPrimitive(src.pattern()))
                .registerTypeAdapter(Color.class, (JsonDeserializer<Color>) (json, typeOfT, context) -> new Color(json.getAsInt()))
                .registerTypeAdapter(Color.class, (JsonSerializer<Color>) (src, typeOfSrc, context) -> new JsonPrimitive(src.getRGB()))
                .registerTypeAdapter(Component.class, (JsonDeserializer<Component>) (json, typeOfT, context) -> CODEC.parse(INSTANCE, json).getOrThrow())
                .registerTypeAdapter(Component.class, (JsonSerializer<Component>) (src, typeOfSrc, context) -> CODEC.encodeStart(INSTANCE, src).getOrThrow());
    }
}
