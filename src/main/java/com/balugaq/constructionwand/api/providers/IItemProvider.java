package com.balugaq.constructionwand.api.providers;

import com.balugaq.constructionwand.core.managers.ConfigManager;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.GameMode;
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
@NullMarked
public interface IItemProvider {
    List<IItemProvider> PROVIDERS = new ArrayList<>();
    int MODIFICATION_BLOCK_LIMIT = ConfigManager.modificationBlockLimit();

    /**
     * Register an item provider, be used when player uses filling wand / building wand
     *
     * @param provider
     *         The item provider
     */
    static void registerProvider(IItemProvider provider) {
        PROVIDERS.add(provider);
    }

    /**
     * Counts the amount of items the player has, If the player is in creative mode, then it will return
     * {@link #MODIFICATION_BLOCK_LIMIT}
     *
     * @param player
     *         The player
     * @param target
     *         The item, item must be one amount
     * @param requireAmount
     *         The max amount to consume
     *
     * @return The amount of items the player has
     */
    static @Range(from = 0, to = Integer.MAX_VALUE) int getItemAmount(Player player, ItemStack target, @Range(from = 1, to = Integer.MAX_VALUE) int requireAmount) {
        if (player.getGameMode() == GameMode.CREATIVE) return MODIFICATION_BLOCK_LIMIT;

        int total = 0;
        for (IItemProvider provider : PROVIDERS) {
            int got = provider.getAmount(player, target, requireAmount);
            requireAmount -= got;
            total += got;
            if (requireAmount <= 0) {
                break;
            }
        }
        return total;
    }

    /**
     * Consume items when player uses filling wand / building wand Call {@link Player#updateInventory()} after calling
     * this method.
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
    @CanIgnoreReturnValue
    static @Range(from = 0, to = Integer.MAX_VALUE) int consumeItems(Player player, ItemStack target, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
        int total = 0;
        for (IItemProvider provider : PROVIDERS) {
            int consumed = provider.consumeItem(player, target, amount);
            amount -= consumed;
            total += consumed;
            if (amount <= 0) {
                break;
            }
        }
        return total;
    }

    /**
     * The plugin that this item provider is from
     *
     * @return The plugin
     */
    Plugin getPlugin();

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
    @CanIgnoreReturnValue
    @Range(from = 0, to = Integer.MAX_VALUE)
    int getAmount(
            Player player,
            ItemStack target,
            @Range(from = 1, to = Integer.MAX_VALUE) int requireAmount
    );

    /**
     * Consume items when player uses filling wand / building wand
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
    @CanIgnoreReturnValue
    @Range(from = 0, to = Integer.MAX_VALUE)
    int consumeItem(
            Player player,
            ItemStack target,
            @Range(from = 1, to = Integer.MAX_VALUE) int amount
    );
}
