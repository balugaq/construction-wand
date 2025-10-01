package com.balugaq.constructionwand.api.events;

import com.balugaq.constructionwand.api.items.BuildingWand;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unused")
@Getter
@NullMarked
public class PrepareBuildingEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final BuildingWand buildingWand;
    private final Block lookingAtBlock;

    public PrepareBuildingEvent(Player player, BuildingWand buildingWand, Block lookingAtBlock) {
        super(player);
        this.buildingWand = buildingWand;
        this.lookingAtBlock = lookingAtBlock;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
