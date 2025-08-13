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
            @NotNull Player player,
            @NotNull Block breakBlock
    ) {
        FakeBlockBreakEvent event = new FakeBlockBreakEvent(breakBlock, player);
        event.callEvent();
        return !event.isCancelled();
    }

    public static FakeBlockBreakEvent simulateBlockBreak(
            @NotNull Player player,
            @NotNull Block breakBlock
    ) {
        FakeBlockBreakEvent event = new FakeBlockBreakEvent(breakBlock, player);
        event.callEvent();
        return event;
    }
}
