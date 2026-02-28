package com.balugaq.constructionwand.core.listeners;

import com.balugaq.constructionwand.api.events.PrepareBreakingEvent;
import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.core.managers.ConfigManager;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.Debug;
import com.balugaq.constructionwand.utils.PermissionUtil;
import com.balugaq.constructionwand.utils.WandUtil;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.checkerframework.checker.index.qual.Positive;
import org.jspecify.annotations.NullMarked;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings("DuplicatedCode")
@NullMarked
public class PrepareBreakingListener implements Listener {
    private static final ModelCuboid BORDER = new ModelCuboid()
            .material(Material.RED_STAINED_GLASS)
            .scale(0.9F, 0.9F, 0.9F);

    @EventHandler(ignoreCancelled = true)
    public void onPrepareBreaking(PrepareBreakingEvent event) {
        if (!ConfigManager.displayProjection()) {
            return;
        }

        Player player = event.getPlayer();
        Debug.debug("Preparing breaking blocks...");
        BreakingWand breakingWand = event.getBreakingWand();
        if (breakingWand.isOpOnly() && !player.isOp()) {
            return;
        }
        showBreakingBlocksFor(player, event.getLookingAtBlock(), breakingWand.getHandleableBlocks(), breakingWand);
    }

    private void showBreakingBlocksFor(Player player, Block lookingAtBlock, @Positive int limitBlocks,
                                       BreakingWand breakingWand) {
        if (!player.isOp() && !PermissionUtil.canBreakBlock(player, lookingAtBlock)) {
            return;
        }

        BlockFace originalFacing = player.getTargetBlockFace(6, FluidCollisionMode.NEVER);
        if (originalFacing == null) {
            return;
        }

        ItemStack item = WandUtil.getItemType(breakingWand, lookingAtBlock);
        if (WandUtil.isItemDisabledToBreak(item)) {
            return;
        }

        Location lookingLocation = lookingAtBlock.getLocation();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        BlockFace lookingFacing = WandUtil.getLookingFacing(originalFacing);

        Set<Location> rawLocations = WandUtil.getRawLocations(
                lookingAtBlock,
                lookingFacing,
                limitBlocks,
                WandUtil.getAxis(itemInMainHand),
                breakingWand.isBlockStrict(),
                true
        );

        World world = lookingLocation.getWorld();
        Map<Location, Double> distances = new HashMap<>();
        for (Location location : rawLocations) {
            if (world.getWorldBorder().isInside(location)) {
                double distance = location.distance(lookingLocation);
                distances.put(location, distance);
            }
        }

        // sort by shortest distance
        Set<Location> locations = new HashSet<>(distances.keySet());
        List<Location> sortedLocations = locations
                .stream()
                .sorted(Comparator.comparingDouble(distances::get))
                .limit(limitBlocks)
                .toList();

        Vector vector = lookingFacing.getOppositeFace().getDirection().multiply(0.6).add(new Vector(0.5F, 0.5F, 0.5F));
        DisplayGroup displayGroup = new DisplayGroup(player.getLocation(), 0.0F, 0.0F);
        for (Location location : sortedLocations) {
            String ls = location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
            Location displayLocation = location.clone().add(vector);
            displayGroup.addDisplay("b" + ls, BORDER.build(displayLocation));
        }

        UUID uuid = player.getUniqueId();

        ConstructionWandPlugin.getInstance().getDisplayManager().registerDisplayGroup(uuid, displayGroup);
    }
}
