package com.balugaq.constructionwand.core.listeners;

import com.balugaq.constructionwand.api.items.Wand;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.WandUtil;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.registry.RebarRegistry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Axis;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

/**
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings("deprecation")
@NullMarked
public class WandModeSwitchListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onWandModeSwitch(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInOffHand = event.getOffHandItem();
        RebarItem wandLike = RebarItem.fromStack(itemInOffHand);
        if (wandLike instanceof Wand) {
            Axis axis = WandUtil.getAxis(itemInOffHand);
            Axis nextAxis;
            if (axis == null) {
                nextAxis = Axis.X;
            } else {
                switch (axis) {
                    case X -> nextAxis = Axis.Y;
                    case Y -> nextAxis = Axis.Z;
                    case Z -> nextAxis = null;
                    default -> throw new AssertionError("Unknown axis: " + axis);
                }
            }

            WandUtil.setAxis(itemInOffHand, nextAxis);
            ItemLore defaultLore = RebarRegistry.ITEMS.get(wandLike.getKey()).getItemStack().getData(DataComponentTypes.LORE);
            if (defaultLore == null || defaultLore.lines().isEmpty()) {
                return;
            }

            var lore = new ArrayList<>(defaultLore.lines());
            lore.add(Component.text()
                 .color(TextColor.color(ChatColor.GOLD.asBungee().getColor().getRGB()))
                 .append(Component.text("Facing strict: " + (nextAxis == null ? "None" : nextAxis.name())))
                 .build());
            itemInOffHand.setData(DataComponentTypes.LORE, ItemLore.lore(lore));

            player.getInventory().setItemInMainHand(itemInOffHand);
            event.setCancelled(true);
            ConstructionWandPlugin.getInstance().getDisplayManager().killDisplays(player.getUniqueId());
            player.sendMessage(Component.text()
                   .color(TextColor.color(ChatColor.GOLD.asBungee().getColor().getRGB()))
                   .append(Component.text("Switched facing to: " + (nextAxis == null ? "None" : nextAxis.name())))
                   .build());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        }
    }
}