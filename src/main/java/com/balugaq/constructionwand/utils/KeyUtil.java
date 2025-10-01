package com.balugaq.constructionwand.utils;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unused")
@UtilityClass
@NullMarked
public class KeyUtil {
    public static final NamespacedKey AXIS = newKey("axis");

    public static NamespacedKey newKey(String key) {
        return new NamespacedKey(ConstructionWandPlugin.getInstance(), key);
    }

    public static NamespacedKey newKey(String pluginName, String key) {
        return new NamespacedKey(pluginName, key);
    }

    public static NamespacedKey newKey(Plugin plugin, String key) {
        return new NamespacedKey(plugin, key);
    }
}
