package com.balugaq.constructionwand.api.providers;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
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
 * @since 1.0
 */
@SuppressWarnings("DuplicatedCode")
@NullMarked
public class ShulkerBoxItemProvider implements ItemProvider {
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
    @SuppressWarnings("UnstableApiUsage")
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

            if (!Tag.SHULKER_BOXES.isTagged(itemStack.getType())) {
                continue;
            }

            ItemContainerContents contents = itemStack.getData(DataComponentTypes.CONTAINER);
            if (contents == null) {
                continue;
            }

            List<ItemStack> stacks = contents.contents();
            if (stacks.isEmpty()) {
                continue;
            }

            for (ItemStack stack : stacks) {
                if (stack.isSimilar(target)) {
                    existing += stack.getAmount();
                }

                if (existing >= requireAmount) {
                    // enough
                    break;
                }
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
    @SuppressWarnings("UnstableApiUsage")
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

            if (!Tag.SHULKER_BOXES.isTagged(itemStack.getType())) {
                continue;
            }

            ItemContainerContents contents = itemStack.getData(DataComponentTypes.CONTAINER);
            if (contents == null) {
                continue;
            }

            List<ItemStack> stacks = new ArrayList<>(contents.contents());
            if (stacks.isEmpty()) {
                continue;
            }

            for (ItemStack stack : stacks) {
                if (stack.getType() == Material.AIR) {
                    continue;
                }

                if (stack.isSimilar(target)) {
                    int exist = stack.getAmount();
                    if (amount >= exist) {
                        stack.setAmount(0);
                        total += exist;
                        amount -= exist;
                    } else {
                        stack.setAmount(exist - amount);
                        total += amount;
                        amount = 0;
                    }

                    if (amount == 0) {
                        break;
                    }
                }
            }

            itemStack.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(stacks));

            if (amount == 0) {
                break;
            }
        }

        return total;
    }
}
