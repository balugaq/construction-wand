package com.balugaq.constructionwand.core.listeners;

import com.balugaq.constructionwand.api.events.PrepareBuildingEvent;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.api.providers.ItemProvider;
import com.balugaq.constructionwand.core.managers.ConfigManager;
import com.balugaq.constructionwand.core.managers.DisplayManager;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.Debug;
import com.balugaq.constructionwand.utils.PermissionUtil;
import com.balugaq.constructionwand.utils.WandUtil;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.UUID;

/**
 * @author balugaq
 * @since 1.0
 */
@NullMarked
public class PrepareBuildingListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPrepareBuilding(PrepareBuildingEvent event) {
        if (!ConfigManager.displayProjection()) return;

        Player player = event.getPlayer();
        Debug.debug("Preparing building blocks...");
        BuildingWand buildingWand = event.getBuildingWand();
        if (buildingWand.isOpOnly() && !player.isOp()) return;

        Block lookingAtBlock = event.getLookingAtBlock();
        int limitBlocks = buildingWand.getLimitBlocks();

        if (!player.isOp() && !PermissionUtil.canPlaceBlock(player, lookingAtBlock, lookingAtBlock)) return;

        ItemStack item = WandUtil.getItemType(buildingWand, lookingAtBlock);
        if (item == null) return;

        int playerHas = ItemProvider.getItemAmount(player, item, limitBlocks);
        Set<Location> showingBlocks = WandUtil.getBuildingLocations(
                player,
                Math.min(limitBlocks, playerHas),
                WandUtil.getAxis(player.getInventory().getItemInMainHand()),
                buildingWand.isBlockStrict(),
                buildingWand.isAllowHandleRebarBlock());

        event.addDisplayLocations(showingBlocks);
    }
}
