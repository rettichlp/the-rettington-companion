package de.rettichlp.therettingtoncompanion.common.configuration;

import de.rettichlp.therettingtoncompanion.common.models.GammaPreset;
import lombok.Data;
import lombok.experimental.Accessors;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static de.rettichlp.therettingtoncompanion.common.models.GammaPreset.OWN_SETTING;
import static de.rettichlp.therettingtoncompanion.common.utils.ModUtils.GSON;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;

@Data
public class Configuration {

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json");

    @Accessors(fluent = true)
    private ChatConfiguration chat = new ChatConfiguration();
    private boolean showArmorHud = true;
    private boolean showArrowHud = true;
    private double ownGammaValue = 0.5;
    private GammaPreset gammaPreset = OWN_SETTING;
    private boolean hideArmor = false;

    public Configuration loadFromFile() {
        File file = CONFIG_PATH.toFile();

        // create a new config if the file does not exist or is empty
        if (!file.exists() || file.length() == 0) {
            LOGGER.info("Config file does not exist or is empty, creating new one at {}", CONFIG_PATH);
            saveToFile();
            return this;
        }

        // load existing config
        try {
            Reader reader = newBufferedReader(CONFIG_PATH);
            return GSON.fromJson(reader, Configuration.class);
        } catch (Exception e) {
            LOGGER.error("Failed to load config from {}", CONFIG_PATH, e);
        }

        // fallback
        LOGGER.warn("Failed to load config, using default values");
        saveToFile();

        return this;
    }

    public void saveToFile() {
        try (Writer writer = newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(this, writer);
            LOGGER.info("Saved config to {}", CONFIG_PATH);
        } catch (IOException e) {
            LOGGER.error("Failed to save config to {}", CONFIG_PATH, e);
        }
    }
}
