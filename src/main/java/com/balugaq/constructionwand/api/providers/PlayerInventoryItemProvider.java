package com.balugaq.constructionwand.api.providers;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.0
 */
@NullMarked
public class PlayerInventoryItemProvider implements ItemProvider {
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
     * @param player        The player
     * @param material      The item material, item must be a pure vanilla item.
     * @param requireAmount The max amount to consume
     * @return The amount of items the player has
     */
    @Override
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int getAmount(Player player, Material material, @Range(from = 1, to = Integer.MAX_VALUE) int requireAmount) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return MODIFICATION_BLOCK_LIMIT;
        }

        int existing = 0;
        ItemStack target = new ItemStack(material, 1);
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            if (itemStack.isSimilar(target)) {
                int count = itemStack.getAmount();
                existing += count;
            }

            if (existing >= requireAmount) {
                // enough
                break;
            }
        }

        return existing;
    }

    /**
     * Consume items when player uses filling wand / building wand
     * Call `player.updateInventory()` after calling this method.
     *
     * @param player   The player
     * @param material The item material, item must be a pure vanilla item.
     * @param amount   The amount to consume
     * @return The amount of items consumed
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int consumeItem(Player player, Material material, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return amount;
        }

        int total = 0;
        ItemStack target = new ItemStack(material, 1);
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            if (!itemStack.isSimilar(target)) {
                continue;
            }

            int count = itemStack.getAmount();
            if (count <= amount) {
                itemStack.setAmount(0);
                total += count;
                amount -= count;
            } else {
                itemStack.setAmount(count - amount);
                total += amount;
                amount = 0;
            }

            if (amount == 0) {
                return total;
            }
        }

        return total;
    }
}
