package com.balugaq.constructionwand.utils;

import com.balugaq.constructionwand.api.events.FakeBlockBreakEvent;
import com.balugaq.constructionwand.api.events.FakeBlockPlaceEvent;
import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.0
 */
@UtilityClass
@NullMarked
public class PermissionUtil {
    public static boolean canPlaceBlock(
            Player player,
            Block placeBlock
    ) {
        return canPlaceBlock(
                player,
                placeBlock,
                placeBlock
        );
    }

    public static boolean canPlaceBlock(
            Player player,
            Block placeBlock,
            Block blockAgainst
    ) {
        return canPlaceBlock(
                placeBlock,
                placeBlock.getState(),
                blockAgainst,
                player.getInventory().getItemInMainHand(),
                player,
                true, // Why needs a `canBuild` param before check permission
                EquipmentSlot.HAND
        );
    }

    public static boolean canPlaceBlock(
            Block placeBlock,
            BlockState replacedBlockState,
            Block blockAgainst,
            ItemStack itemInMainHand,
            Player player,
            boolean canBuild,
            EquipmentSlot hand
    ) {
        FakeBlockPlaceEvent event = simulateBlockPlace(placeBlock, replacedBlockState, blockAgainst, itemInMainHand, player, canBuild, hand);
        return !event.isCancelled();
    }

    public static FakeBlockPlaceEvent simulateBlockPlace(
            Block placeBlock,
            BlockState replacedBlockState,
            Block blockAgainst,
            ItemStack itemInMainHand,
            Player player,
            boolean canBuild,
            EquipmentSlot hand
    ) {
        FakeBlockPlaceEvent event = new FakeBlockPlaceEvent(
                placeBlock,
                replacedBlockState,
                blockAgainst,
                itemInMainHand,
                player,
                canBuild,
                hand
        );
        event.callEvent();
        return event;
    }

    public static boolean canBreakBlock(
            Player player,
            Block theBlock
    ) {
        FakeBlockBreakEvent event = simulateBlockBreak(player, theBlock);
        return !event.isCancelled();
    }

    public static FakeBlockBreakEvent simulateBlockBreak(
            Player player,
            Block theBlock
    ) {
        FakeBlockBreakEvent event = new FakeBlockBreakEvent(theBlock, player);
        event.callEvent();
        return event;
    }
}
