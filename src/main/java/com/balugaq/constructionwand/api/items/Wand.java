package com.balugaq.constructionwand.api.items;

import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

/**
 * @author balugaq
 * @since 1.0
 */
@NullMarked
public interface Wand extends Keyed {
    Map<NamespacedKey, Map<String, Object>> CACHE = new HashMap<>();

    boolean isBlockStrict();

    boolean isOpOnly();

    int getLimitBlocks();

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
