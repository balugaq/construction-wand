package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.core.tasks.BlockPreviewTask;
import com.balugaq.constructionwand.core.tasks.DisplaysClearTask;
import com.balugaq.constructionwand.core.tasks.FillWandSUITask;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Getter
public class DisplayManager implements IManager {
    private final Map<UUID, BlockFace> lookingFaces = new HashMap<>();
    private final Map<UUID, Location> lookingAts = new HashMap<>();
    private final Map<UUID, DisplayGroup> displays = new HashMap<>();
    private final @NotNull JavaPlugin plugin;
    private boolean running = true;

    public DisplayManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        ConfigManager config = ConstructionWandPlugin.getInstance().getConfigManager();
        // Entities needs to running on the main thread
        Bukkit.getScheduler().runTaskTimer(plugin, () -> new BlockPreviewTask().run(), 1, config.getBlockPreviewTaskPeriod());
        Bukkit.getScheduler().runTaskTimer(plugin, () -> new DisplaysClearTask().run(), 1, config.getDisplaysClearTaskPeriod());

        // Particles are asyncable
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> new FillWandSUITask().run(), 1, config.getFillWandSUITaskPeriod());
    }

    @Override
    public void shutdown() {
        stopTasks();
    }

    public void stopTasks() {
        running = false;
        for (UUID uuid : new HashSet<>(displays.keySet())) {
            killDisplays(uuid);
        }
    }

    public void killDisplays(@NotNull UUID uuid) {
        DisplayGroup group = displays.get(uuid);
        if (group != null) {
            group.remove();
        }
        displays.remove(uuid);
        lookingAts.remove(uuid);
        lookingFaces.remove(uuid);
    }

    public void registerDisplayGroup(@NotNull UUID uuid, @NotNull DisplayGroup group) {
        displays.put(uuid, group);
    }
}
