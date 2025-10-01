package com.balugaq.constructionwand.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FakeBlockBreakEvent extends BlockBreakEvent implements FakeEvent {
    @SuppressWarnings("UnstableApiUsage")
    public FakeBlockBreakEvent(Block theBlock, Player player) {
        super(theBlock, player);
    }
}
