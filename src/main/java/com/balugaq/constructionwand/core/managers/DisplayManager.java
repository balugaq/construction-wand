package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.DisplayType;
import com.balugaq.constructionwand.api.display.PreviewUpdateRequest;
import com.balugaq.constructionwand.core.tasks.BlockPreviewTask;
import com.balugaq.constructionwand.core.tasks.DisplaysClearTask;
import com.balugaq.constructionwand.core.tasks.FillWandSUITask;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.Debug;
import com.balugaq.constructionwand.utils.WandUtil;
import com.google.common.collect.MapMaker;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author balugaq
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
    private final ConcurrentHashMap<UUID, Map<Location, PreviewUpdateRequest>> requests = new ConcurrentHashMap<>();
    private final Map<UUID, Set<Location>> locations = new HashMap<>();
    private boolean running = true;

    public DisplayManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        ConfigManager config = ConstructionWandPlugin.getInstance().getConfigManager();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> new BlockPreviewTask().run(), 1,
                                           config.getBlockPreviewTaskPeriod());
        Bukkit.getScheduler().runTaskTimer(plugin, () -> new DisplaysClearTask().run(), 1,
                                           config.getDisplaysClearTaskPeriod());
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
            Debug.debug("Killed displays for " + uuid);
        }
        displays.remove(uuid);
        lookingAts.remove(uuid);
        lookingFaces.remove(uuid);
        locations.remove(uuid);
    }

    public void updateDisplays(Player player, Set<Location> coming, Material material, DisplayType displayType) {
        UUID uuid = player.getUniqueId();
        DisplayGroup group = displays.getOrDefault(uuid, new DisplayGroup(player.getLocation(), 0.0F, 0.0F));

        Set<Location> origin = locations.getOrDefault(uuid, ConcurrentHashMap.newKeySet());

        // Find the origin that does not exist in locations and call group.removeDisplay()
        origin.stream().filter(location -> !coming.contains(location)).toList().forEach(location -> {
            requestRemoveDisplay(player, group, location);
        });
        Location lookingAt = lookingAts.get(uuid);
        if (lookingAt != null)
            requestRemoveDisplay(player, group, lookingAt);

        BlockFace originFacing = player.getTargetBlockFace(6, FluidCollisionMode.NEVER);
        if (originFacing != null) {
            // Find the locations that do not exist in origin and call group.addDisplay()
            coming.stream().filter(location -> !origin.contains(location)).forEach(location -> {
                requestAddDisplay(player, group, location, displayType, material, originFacing);
            });
        }

        if (!displays.containsKey(uuid)) {
            displays.put(uuid, group);
        }
    }

    public void requestRemoveDisplay(Player player, DisplayGroup group, Location location) {
        requests.putIfAbsent(player.getUniqueId(), createMap());
        requests.get(player.getUniqueId()).put(location, new PreviewUpdateRequest.Remove(player, group, location));
        locations.putIfAbsent(player.getUniqueId(), ConcurrentHashMap.newKeySet());
        locations.get(player.getUniqueId()).remove(location);
        if (ConfigManager.debug()) {
            Debug.debug("Scheduled to remove displays at " + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ());
        }
    }

    private <K, V> Map<K, V> createMap() {
        return new MapMaker().concurrencyLevel(4).makeMap();
    }

    public void requestAddDisplay(Player player, DisplayGroup group, Location location, DisplayType displayType, Material material, BlockFace originFacing) {
        requests.putIfAbsent(player.getUniqueId(), createMap());
        var currentRequest = requests.get(player.getUniqueId()).get(location);
        if (!(currentRequest instanceof PreviewUpdateRequest.Remove)) {
            requests.get(player.getUniqueId()).put(location, new PreviewUpdateRequest.Add(player, group, location, displayType, material, originFacing));
            locations.putIfAbsent(player.getUniqueId(), ConcurrentHashMap.newKeySet());
            locations.get(player.getUniqueId()).add(location);
            if (ConfigManager.debug()) {
                Debug.debug("Scheduled to add displays at " + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ());
            }
        }
    }

    public boolean removeDisplay(DisplayGroup group, Location location) {
        boolean result = false;
        var display = group.removeDisplay("b_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ());
        if (display != null) {
            display.remove();
            result = true;
        }
        display = group.removeDisplay("m_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ());
        if (display != null) display.remove();
        if (ConfigManager.debug()) {
            Debug.debug("Removed display at " + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ());
        }
        return result;
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
        if (ConfigManager.debug()) {
            Debug.debug("Added display at " + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ());
        }
    }

    public <T extends Entity> T tagMeta(T entity) {
        entity.setMetadata(plugin.getName(),
                            new FixedMetadataValue(ConstructionWandPlugin.getInstance(), true));
        return entity;
    }
}
