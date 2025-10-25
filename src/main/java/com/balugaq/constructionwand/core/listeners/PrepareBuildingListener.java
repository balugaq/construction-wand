package com.balugaq.constructionwand.core.listeners;

import com.balugaq.constructionwand.api.events.PrepareBuildingEvent;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.api.providers.ItemProvider;
import com.balugaq.constructionwand.core.managers.ConfigManager;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.Debug;
import com.balugaq.constructionwand.utils.PermissionUtil;
import com.balugaq.constructionwand.utils.WandUtil;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jspecify.annotations.NullMarked;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;

import java.util.Set;
import java.util.UUID;

/**
 * @author balugaq
 * @since 1.0
 */
@NullMarked
public class PrepareBuildingListener implements Listener {
    private static final ModelCuboid BLOCK_BASE = new ModelCuboid()
            .scale(0.6F, 0.6F, 0.6F);
    private static final ModelCuboid BORDER = new ModelCuboid()
            .material(Material.LIGHT_GRAY_STAINED_GLASS)
            .scale(0.7F, 0.7F, 0.7F);

    @EventHandler(ignoreCancelled = true)
    public void onPrepareBuilding(PrepareBuildingEvent event) {
        if (!ConfigManager.displayProjection()) {
            return;
        }

        Player player = event.getPlayer();
        Debug.debug("Preparing building blocks...");
        BuildingWand buildingWand = event.getBuildingWand();
        if (buildingWand.isOpOnly() && !player.isOp()) {
            return;
        }
        showBuildingBlocksFor(player, event.getLookingAtBlock(), buildingWand.getLimitBlocks(), event.getBuildingWand());
    }

    private void showBuildingBlocksFor(Player player, Block lookingAtBlock, int limitBlocks, BuildingWand buildingWand) {
        if (!player.isOp() && !PermissionUtil.canPlaceBlock(player, lookingAtBlock)) {
            return;
        }
        Material material = lookingAtBlock.getType();
        int playerHas = ItemProvider.getItemAmount(player, material, limitBlocks);

        Set<Location> showingBlocks = WandUtil.getBuildingLocations(player, Math.min(limitBlocks, playerHas), WandUtil.getAxis(player.getInventory().getItemInMainHand()), buildingWand.isBlockStrict());
        DisplayGroup displayGroup = new DisplayGroup(player.getLocation(), 0.0F, 0.0F);
        for (Location location : showingBlocks) {
            String ls = location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
            Location displayLocation = location.clone().add(0.5, 0.5, 0.5);
            displayGroup.addDisplay("m" + ls, BLOCK_BASE.material(material).build(displayLocation));
            displayGroup.addDisplay("b" + ls, BORDER.build(displayLocation));
        }

        displayGroup.getDisplays().forEach((name, display) ->
                display.setMetadata(ConstructionWandPlugin.getInstance().getName(), new FixedMetadataValue(ConstructionWandPlugin.getInstance(), true))
        );


        UUID uuid = player.getUniqueId();

        ConstructionWandPlugin.getInstance().getDisplayManager().registerDisplayGroup(uuid, displayGroup);
    }
}
