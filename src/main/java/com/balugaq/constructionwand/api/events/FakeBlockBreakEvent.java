package com.balugaq.constructionwand.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class FakeBlockBreakEvent extends BlockBreakEvent implements IFakeEvent {
    @SuppressWarnings("UnstableApiUsage")
    public FakeBlockBreakEvent(Block theBlock, Player player) {
        super(theBlock, player);
    }
}
