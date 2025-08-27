package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.core.managers.ConfigManager;
import org.bukkit.Keyed;
import org.jetbrains.annotations.NotNull;

public interface Wand extends Keyed {
    boolean isBlockStrict();

    boolean isOpOnly();

    int getLimitBlocks();

    long getCooldown();

    default <T> @NotNull T getOrThrow(@NotNull String key, @NotNull Class<T> clazz) {
        return ConfigManager.getSettingOrThrow(getKey(), key, clazz);
    }
}
