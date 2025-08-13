package com.balugaq.constructionwand.utils;

import com.balugaq.constructionwand.api.events.FakeBlockBreakEvent;
import com.balugaq.constructionwand.api.events.FakeBlockPlaceEvent;
import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class PermissionUtil {
    public static boolean canPlaceBlock(
            @NotNull Player player,
            @NotNull Block placeBlock
    ) {
        return canPlaceBlock(
                player,
                placeBlock,
                placeBlock
        );
    }

    public static boolean canPlaceBlock(
            @NotNull Player player,
            @NotNull Block placeBlock,
            @NotNull Block blockAgainst
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
            @NotNull Block placeBlock,
            @NotNull BlockState replacedBlockState,
            @NotNull Block blockAgainst,
            @NotNull ItemStack itemInMainHand,
            @NotNull Player player,
            boolean canBuild,
            @NotNull EquipmentSlot hand
    ) {
        FakeBlockPlaceEvent event = simulateBlockPlace(placeBlock, replacedBlockState, blockAgainst, itemInMainHand, player, canBuild, hand);
        return !event.isCancelled();
    }

    public static @NotNull FakeBlockPlaceEvent simulateBlockPlace(
            @NotNull Block placeBlock,
            @NotNull BlockState replacedBlockState,
            @NotNull Block blockAgainst,
            @NotNull ItemStack itemInMainHand,
            @NotNull Player player,
            boolean canBuild,
            @NotNull EquipmentSlot hand
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
            @NotNull Player player,
            @NotNull Block theBlock
    ) {
        FakeBlockBreakEvent event = simulateBlockBreak(player, theBlock);
        return !event.isCancelled();
    }

    public static @NotNull FakeBlockBreakEvent simulateBlockBreak(
            @NotNull Player player,
            @NotNull Block theBlock
    ) {
        FakeBlockBreakEvent event = new FakeBlockBreakEvent(theBlock, player);
        event.callEvent();
        return event;
    }
}
