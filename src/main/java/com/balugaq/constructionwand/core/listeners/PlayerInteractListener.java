package com.balugaq.constructionwand.core.listeners;

import com.balugaq.constructionwand.api.items.Wand;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * When a player hold a wand in his main hand and hold a block in his offhand,
 * the event is cancelled to prevent the player from placing blocks with the wand.
 * I don't know why {@link BlockPlaceEvent} or {@link Block#setType(Material)}
 * will make player interact with main hand again.
 * So this listener is important for playing building wand.
 *
 * @author balugaq
 * @since 1.0
 */
@NullMarked
public class PlayerInteractListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        PylonItem pyItem = PylonItem.fromStack(itemInMainHand);
        if (pyItem instanceof Wand) {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (itemInOffHand != null && itemInOffHand.getType() != Material.AIR && itemInOffHand.getType().isBlock()) {
                event.setCancelled(true);
            }
        }
    }
}
