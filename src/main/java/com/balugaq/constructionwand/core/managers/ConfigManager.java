package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.Debug;
import io.github.pylonmc.pylon.core.config.Settings;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ConfigManager implements IManager {
    public static final Map<NamespacedKey, Map<String, ?>> CACHE = new HashMap<>();
    private final @NotNull JavaPlugin plugin;
    private final boolean autoUpdate;
    private final boolean displayProjection;
    private final boolean debug;
    private final int blockPreviewTaskPeriod;
    private final int displaysClearTaskPeriod;
    private final int fillWandSUITaskPeriod;

    public ConfigManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        autoUpdate = config.getBoolean("auto-update");
        displayProjection = config.getBoolean("display-projection");
        debug = config.getBoolean("debug");
        blockPreviewTaskPeriod = config.getInt("tasks.block-preview.period");
        displaysClearTaskPeriod = config.getInt("tasks.displays-clear.period");
        fillWandSUITaskPeriod = config.getInt("tasks.fill-wand-sui.period");
    }

    public static <T> @NotNull T getSettingOrThrow(@NotNull NamespacedKey nk, @NotNull String key, @NotNull Class<T> clazz) {
        Map<String, ?> cache = CACHE.get(nk);
        if (cache == null) {
            T value = Settings.get(nk).getOrThrow(key, clazz);
            CACHE.put(nk, Map.of(key, value));
            return value;
        } else {
            Object value = cache.get(key);
            if (value == null) {
                value = Settings.get(nk).getOrThrow(key, clazz);
                CACHE.put(nk, Map.of(key, value));
                return clazz.cast(value);
            } else {
                return clazz.cast(value);
            }
        }
    }

    public static boolean autoUpdate() {
        return ConstructionWandPlugin.getInstance().getConfigManager().autoUpdate;
    }

    public static boolean displayProjection() {
        return ConstructionWandPlugin.getInstance().getConfigManager().displayProjection;
    }

    public static boolean debug() {
        return ConstructionWandPlugin.getInstance().getConfigManager().debug;
    }

    @Override
    public void setup() {
        loadConfig();
    }

    @Override
    public void shutdown() {

    }

    public void loadConfig() {
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        final InputStream inputStream = plugin.getResource("config.yml");
        final File existingFile = new File(plugin.getDataFolder(), "config.yml");

        if (inputStream == null) {
            return;
        }

        final Reader reader = new InputStreamReader(inputStream);
        final FileConfiguration resourceConfig = YamlConfiguration.loadConfiguration(reader);
        final FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(existingFile);

        for (String key : resourceConfig.getKeys(false)) {
            checkKey(existingConfig, resourceConfig, key);
        }

        try {
            existingConfig.save(existingFile);
        } catch (IOException e) {
            Debug.log(e);
        }
    }

    private void checkKey(@NotNull FileConfiguration existingConfig, @NotNull FileConfiguration resourceConfig, @NotNull String key) {
        final Object currentValue = existingConfig.get(key);
        final Object newValue = resourceConfig.get(key);
        if (newValue instanceof ConfigurationSection section) {
            for (String sectionKey : section.getKeys(false)) {
                checkKey(existingConfig, resourceConfig, key + "." + sectionKey);
            }
        } else if (currentValue == null) {
            existingConfig.set(key, newValue);
        }
    }
}
