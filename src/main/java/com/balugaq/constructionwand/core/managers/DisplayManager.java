package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.DisplayType;
import com.balugaq.constructionwand.core.tasks.BlockPreviewTask;
import com.balugaq.constructionwand.core.tasks.DisplaysClearTask;
import com.balugaq.constructionwand.core.tasks.FillWandSUITask;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.WandUtil;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author balugaq
 * @since 1.0
 */
@Getter
@NullMarked
public class DisplayManager implements IManager {
    public static final ModelCuboid BUILD_BLOCK_BASE = new ModelCuboid()
            .scale(0.6F, 0.6F, 0.6F);
    public static final ModelCuboid BUILD_BORDER = new ModelCuboid()
            .material(Material.LIGHT_GRAY_STAINED_GLASS)
            .scale(0.7F, 0.7F, 0.7F);
    public static final ModelCuboid BREAK_BORDER = new ModelCuboid()
            .material(Material.RED_STAINED_GLASS)
            .scale(0.9F, 0.9F, 0.9F);
    private final Map<UUID, BlockFace> lookingFaces = new HashMap<>();
    private final Map<UUID, Location> lookingAts = new HashMap<>();
    private final Map<UUID, DisplayGroup> displays = new HashMap<>();
    private final JavaPlugin plugin;
    private boolean running = true;

    public DisplayManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        ConfigManager config = ConstructionWandPlugin.getInstance().getConfigManager();
        // Entities needs to running on the main thread
        Bukkit.getScheduler().runTaskTimer(plugin, () -> new BlockPreviewTask().run(), 1,
                                           config.getBlockPreviewTaskPeriod());
        Bukkit.getScheduler().runTaskTimer(plugin, () -> new DisplaysClearTask().run(), 1,
                                           config.getDisplaysClearTaskPeriod());

        // Particles are asyncable
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> new FillWandSUITask().run(), 1,
                                                         config.getFillWandSUITaskPeriod());
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

    public void killDisplays(UUID uuid) {
        DisplayGroup group = displays.get(uuid);
        if (group != null) {
            group.remove();
        }
        displays.remove(uuid);
        lookingAts.remove(uuid);
        lookingFaces.remove(uuid);
    }

    public void updateDisplays(Player player, Set<Location> locations, Material material, DisplayType displayType) {
        UUID uuid = player.getUniqueId();
        DisplayGroup group = displays.getOrDefault(uuid, new DisplayGroup(player.getLocation(), 0.0F, 0.0F));

        Set<Location> origin = group.getDisplays().keySet().stream().filter(name -> name.startsWith("b")).map(
                name -> {
                    var split = name.split("_");
                    return new Location(group.getLocation().getWorld(), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
                }
        ).collect(Collectors.toSet());

        // Find the origin that does not exist in locations and call group.removeDisplay()
        origin.stream().filter(location -> !locations.contains(location)).forEach(location -> {
            removeDisplay(group, location);
        });
        Location lookingAt = lookingAts.get(uuid);
        if (lookingAt != null)
            removeDisplay(group, lookingAt);

        BlockFace originFacing = player.getTargetBlockFace(6, FluidCollisionMode.NEVER);
        if (originFacing != null) {
            // Find the locations that do not exist in origin and call group.addDisplay()
            locations.stream().filter(location -> !origin.contains(location)).forEach(location -> {
                addDisplay(group, location, displayType, material, originFacing);
            });
            if (lookingAt != null) {
                addDisplay(group, lookingAt, displayType, material, originFacing);
            }
        }

        if (!displays.containsKey(uuid)) {
            registerDisplayGroup(uuid, group);
        }
    }

    public void removeDisplay(DisplayGroup group, Location location) {
        var display = group.removeDisplay("b_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ());
        if (display != null) display.remove();
        display = group.removeDisplay("m_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ());
        if (display != null) display.remove();
    }

    public void addDisplay(DisplayGroup group, Location location, DisplayType displayType, Material material, BlockFace originFacing) {
        String ls = location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
        Location displayLocation = location.clone().add(0.5, 0.5, 0.5);
        switch (displayType) {
            case BUILD -> {
                group.addDisplay("m_" + ls, tagMeta(BUILD_BLOCK_BASE.material(material).build(displayLocation)));
                group.addDisplay("b_" + ls, tagMeta(BUILD_BORDER.build(displayLocation)));
            }
            case BREAK -> {
                Vector vector = WandUtil.getLookingFacing(originFacing).getOppositeFace().getDirection().multiply(0.6).add(new Vector(0, 0.1F, 0));
                group.addDisplay("b_" + ls, tagMeta(BREAK_BORDER.build(displayLocation.clone().add(vector))));
            }
        }
    }

    public <T extends Entity> T tagMeta(T entity) {
        entity.setMetadata(plugin.getName(),
                            new FixedMetadataValue(ConstructionWandPlugin.getInstance(), true));
        return entity;
    }

    public void registerDisplayGroup(UUID uuid, DisplayGroup group) {
        displays.put(uuid, group);
    }
}
