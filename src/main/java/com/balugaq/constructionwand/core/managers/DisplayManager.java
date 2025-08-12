package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.events.PrepareBreakingEvent;
import com.balugaq.constructionwand.api.events.PrepareBuildingEvent;
import com.balugaq.constructionwand.api.interfaces.IManager;
import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.api.items.FillWand;
import com.balugaq.constructionwand.core.tasks.BlockPreviewTask;
import com.balugaq.constructionwand.core.tasks.DisplaysClearTask;
import com.balugaq.constructionwand.core.tasks.FillWandSUITask;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.ParticleUtil;
import com.balugaq.constructionwand.utils.WandUtil;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.pylonmc.pylon.core.item.PylonItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class DisplayManager implements IManager {
    private final Map<UUID, BlockFace> lookingFaces = new HashMap<>();
    private final Map<UUID, Location> lookingAt = new HashMap<>();
    private final Map<UUID, DisplayGroup> displays = new HashMap<>();
    private final JavaPlugin plugin;
    private boolean running = true;

    public DisplayManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        // Entities needs to running on the main thread
        Bukkit.getScheduler().runTaskTimer(plugin, BlockPreviewTask::new, 1, 1);
        Bukkit.getScheduler().runTaskTimer(plugin, DisplaysClearTask::new, 1, 20 * 60 * 5); // 5 minutes

        // Particles are asyncable
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, FillWandSUITask::new, 0, 10);
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
        lookingAt.remove(uuid);
        lookingFaces.remove(uuid);
    }

    public void registerDisplayGroup(@NotNull UUID uuid, @NotNull DisplayGroup group) {
        displays.put(uuid, group);
    }
}
