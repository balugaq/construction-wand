package com.balugaq.constructionwand.core.integrations.pylon;

import com.balugaq.constructionwand.api.providers.IItemProvider;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import io.github.pylonmc.pylon.content.machines.storage.Silo;
import io.github.pylonmc.rebar.item.RebarItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 */
@SuppressWarnings("DuplicatedCode")
@NullMarked
public class SiloItemProvider implements IItemProvider {
    /**
     * The plugin that this item provider is from
     *
     * @return The plugin
     */
    @Override
    public Plugin getPlugin() {
        return ConstructionWandPlugin.getInstance();
    }

    /**
     * Counts the amount of items the player has
     *
     * @param player
     *         The player
     * @param target
     *         The item
     * @param requireAmount
     *         The max amount to consume
     *
     * @return The amount of items the player has
     */
    @SuppressWarnings("UnstableApiUsage")
    @Override
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int getAmount(Player player, ItemStack target, @Range(from = 1, to = Integer.MAX_VALUE) int requireAmount) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return MODIFICATION_BLOCK_LIMIT;
        }

        int existing = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (!(RebarItem.fromStack(itemStack) instanceof Silo.Item silo)) {
                continue;
            }

            if (!target.isSimilar(silo.getSiloStack())) {
                continue;
            }

            if (silo.getSiloAmount() == null) {
                continue;
            }

            existing += silo.getSiloAmount();
            if (existing >= requireAmount) {
                return requireAmount;
            }
        }

        return existing;
    }

    /**
     * Consume items when player uses filling wand / building wand Call `player.updateInventory()` after calling this
     * method.
     *
     * @param player
     *         The player
     * @param target
     *         The item
     * @param amount
     *         The amount to consume
     *
     * @return The amount of items consumed
     */
    @SuppressWarnings("UnstableApiUsage")
    @Override
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int consumeItem(Player player, ItemStack target, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return amount;
        }

        int total = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (!(RebarItem.fromStack(itemStack) instanceof Silo.Item silo)) {
                continue;
            }

            if (!target.isSimilar(silo.getSiloStack())) {
                continue;
            }

            if (silo.getSiloAmount() == null) {
                continue;
            }

            if (amount > silo.getSiloAmount()) {
                total += silo.getSiloAmount();
                amount -= silo.getSiloAmount();
                silo.setSiloAmount(0);
            } else {
                total += amount;
                silo.setSiloAmount(silo.getSiloAmount() - amount);
                amount = 0;
            }

            if (amount == 0) {
                break;
            }
        }

        return total;
    }
}
