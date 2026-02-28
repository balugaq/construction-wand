package com.balugaq.constructionwand.core.listeners;

import com.balugaq.constructionwand.api.events.PrepareBreakingEvent;
import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.core.managers.ConfigManager;
import com.balugaq.constructionwand.utils.Debug;
import com.balugaq.constructionwand.utils.PermissionUtil;
import com.balugaq.constructionwand.utils.WandUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings("DuplicatedCode")
@NullMarked
public class PrepareBreakingListener implements Listener {

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

        Block lookingAtBlock = event.getLookingAtBlock();
        int limitBlocks = breakingWand.getHandleableBlocks();
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
                true,
                breakingWand.isAllowHandleRebarBlock()
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

        event.addDisplayLocations(sortedLocations);
    }
}
