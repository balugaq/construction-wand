package com.balugaq.constructionwand.api.events;

import com.balugaq.constructionwand.api.items.BreakingWand;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings("unused")
@Getter
@NullMarked
public class PrepareBreakingEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final BreakingWand breakingWand;
    private final Block lookingAtBlock;

    public PrepareBreakingEvent(Player player, BreakingWand breakingWand, Block lookingAtBlock) {
        super(player);
        this.breakingWand = breakingWand;
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
