package com.balugaq.constructionwand.api.items;

import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

/**
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings("SameReturnValue")
@NullMarked
public interface Wand extends Keyed {
    Map<NamespacedKey, Map<String, Object>> CACHE = new HashMap<>();

    boolean isBlockStrict();

    boolean isOpOnly();

    boolean isDisabled();

    int getLimitBlocks();

    boolean isAllowHandlePylonBlock();

    @Range(from = -1, to = Integer.MAX_VALUE)
    int getDurability();

    ItemStack getStack();

    int getCooldownTicks();

    default int getHandleableBlocks() {
        return Math.max(getLimitBlocks(), getDurability());
    }

    default void handleInteract(Player player, int blocks) {
        getStack().damage(blocks, player);
        if (getCooldownTicks() > 0) {
            player.setCooldown(getStack(), getCooldownTicks());
        }
    }

    @SuppressWarnings("unchecked")
    default <T> T getOrThrow(String key, ConfigAdapter<T> adapter) {
        NamespacedKey ik = getKey();
        if (!CACHE.containsKey(ik)) {
            CACHE.put(ik, new HashMap<>());
        }

        if (CACHE.get(ik).containsKey(key)) {
            return (T) CACHE.get(ik).get(key);
        }

        T v = Settings.get(ik).getOrThrow(key, adapter);
        CACHE.get(ik).put(key, v);
        return v;
    }
}
