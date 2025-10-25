package com.balugaq.constructionwand.api.items;

import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

/**
 * @author balugaq
 * @since 1.0
 */
public interface Wand extends Keyed {
    Map<NamespacedKey, Map<String, Object>> cache = new HashMap<>();

    boolean isBlockStrict();

    boolean isOpOnly();

    int getLimitBlocks();

    default <T> T getOrThrow(String key, ConfigAdapter<T> adapter) {
        NamespacedKey ik = getKey();
        if (!cache.containsKey(ik)) {
            cache.put(ik, new HashMap<>());
        }

        if (cache.get(ik).containsKey(key)) {
            return (T) cache.get(ik).get(key);
        }

        T v = Settings.get(ik).getOrThrow(key, adapter);
        cache.get(ik).put(key, v);
        return v;
    }
}
