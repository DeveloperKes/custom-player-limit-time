package net.kesgroom.cplt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CustomConfigManager {
    private static final File CONFIG_FILE = new File("config/playtimelimiter.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static class Config {
        public long maxPlayTime = 3 * 3600000L; // Valor predeterminado
        public boolean isTimeLimitEnabled = false;
    }

    public static Config loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                return GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        saveConfig(new Config());
        return new Config();
    }

    public static void saveConfig(Config config) {
        CONFIG_FILE.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetConfig() {
        saveConfig(new Config());
    }

    public static void setMaxPlayTime(long maxPlayTime) {
        Config config = loadConfig();
        config.maxPlayTime = maxPlayTime;
        saveConfig(config);
    }

    public static void setTimeLimitEnabled(boolean isTimeLimitEnabled) {
        Config config = loadConfig();
        config.isTimeLimitEnabled = isTimeLimitEnabled;
        saveConfig(config);
    }

    public static long getTimeLimit() {
        Config config = loadConfig();
        return config.maxPlayTime;
    }

    public static boolean getEnabledTimeLimit() {
        Config config = loadConfig();
        return config.isTimeLimitEnabled;
    }
}
