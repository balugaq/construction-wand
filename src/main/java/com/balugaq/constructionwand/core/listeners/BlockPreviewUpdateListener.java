package com.balugaq.constructionwand.core.listeners;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

/**
 * @author balugaq
 */
public class BlockPreviewUpdateListener implements Listener {
    @EventHandler
    public void onSwitchItem(PlayerItemHeldEvent event) {
        ConstructionWandPlugin.getInstance().getDisplayManager().killDisplays(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onSwitchWorld(PlayerChangedWorldEvent event) {
        ConstructionWandPlugin.getInstance().getDisplayManager().killDisplays(event.getPlayer().getUniqueId());
        ConstructionWandPlugin.getInstance().getDisplayManager().getDisplays().remove(event.getPlayer().getUniqueId());
    }
}
