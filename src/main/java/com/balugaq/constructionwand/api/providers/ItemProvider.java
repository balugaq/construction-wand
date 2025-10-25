package com.balugaq.constructionwand.api.providers;

import com.balugaq.constructionwand.core.managers.ConfigManager;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author balugaq
 * @since 1.0
 */
@NullMarked
public interface ItemProvider {
    List<ItemProvider> PROVIDERS = new CopyOnWriteArrayList<>();
    int MAX_AMOUNT = ConfigManager.modificationBlockLimit();

    /**
     * Register an item provider, be used when player uses filling wand / building wand
     *
     * @param provider The item provider
     */
    static void registerProvider(ItemProvider provider) {
        PROVIDERS.add(provider);
    }

    /**
     * Counts the amount of items the player has
     *
     * @param player        The player
     * @param material      The item material, item must be a pure vanilla item.
     * @param requireAmount The max amount to consume
     * @return The amount of items the player has
     */
    static @Range(from = 0, to = Integer.MAX_VALUE) int getItemAmount(Player player, Material material, @Range(from = 1, to = Integer.MAX_VALUE) int requireAmount) {
        int total = 0;
        for (ItemProvider provider : PROVIDERS) {
            int got = provider.getAmount(player, material, requireAmount);
            requireAmount -= got;
            total += got;
            if (requireAmount <= 0) {
                break;
            }
        }
        return total;
    }

    /**
     * Consume items when player uses filling wand / building wand
     * Call {@link Player#updateInventory()} after calling this method.
     *
     * @param player   The player
     * @param material The item material, item must be a pure vanilla item.
     * @param amount   The amount to consume
     * @return The amount of items consumed
     */
    @CanIgnoreReturnValue
    static @Range(from = 0, to = Integer.MAX_VALUE) int consumeItems(Player player, Material material, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
        int total = 0;
        for (ItemProvider provider : PROVIDERS) {
            int consumed = provider.consumeItem(player, material, amount);
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
     * @param player        The player
     * @param material      The item material, item must be a pure vanilla item.
     * @param requireAmount The max amount to consume
     * @return The amount of items the player has
     */
    @CanIgnoreReturnValue
    @Range(from = 0, to = Integer.MAX_VALUE)
    int getAmount(
            Player player,
            Material material,
            @Range(from = 1, to = Integer.MAX_VALUE) int requireAmount
    );

    /**
     * Consume items when player uses filling wand / building wand
     *
     * @param player   The player
     * @param material The item material, item must be a pure vanilla item.
     * @param amount   The amount to consume
     * @return The amount of items consumed
     */
    @CanIgnoreReturnValue
    @Range(from = 0, to = Integer.MAX_VALUE)
    int consumeItem(
            Player player,
            Material material,
            @Range(from = 1, to = Integer.MAX_VALUE) int amount
    );
}
