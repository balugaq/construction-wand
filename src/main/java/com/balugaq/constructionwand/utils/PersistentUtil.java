package com.balugaq.constructionwand.utils;

import io.papermc.paper.persistence.PersistentDataViewHolder;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unused")
@UtilityClass
@NullMarked
public class PersistentUtil {
    @Contract("null, _, _ -> null")
    @Nullable
    public static <T, Z> Z get(@Nullable PersistentDataViewHolder holder, PersistentDataType<T, Z> dataType, NamespacedKey key) {
        if (holder == null) {
            return null;
        }
        return holder.getPersistentDataContainer().get(key, dataType);
    }

    public static <T, Z> void set(@Nullable ItemStack itemStack, PersistentDataType<T, Z> dataType, NamespacedKey key, Z value) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        set(meta, dataType, key, value);
        itemStack.setItemMeta(meta);
    }


    public static <T, Z> void set(@Nullable PersistentDataHolder holder, PersistentDataType<T, Z> dataType, NamespacedKey key, Z value) {
        if (holder == null) {
            return;
        }

        holder.getPersistentDataContainer().set(key, dataType, value);
    }

    public static void remove(@Nullable ItemStack itemStack, NamespacedKey key) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        remove(meta, key);
        itemStack.setItemMeta(meta);
    }

    public static void remove(@Nullable PersistentDataHolder holder, NamespacedKey key) {
        if (holder == null) {
            return;
        }

        holder.getPersistentDataContainer().remove(key);
    }

    @Contract("null, _ -> false")
    public static <T, Z> boolean has(@Nullable PersistentDataViewHolder holder, NamespacedKey key) {
        if (holder == null) {
            return false;
        }

        return holder.getPersistentDataContainer().has(key);
    }

    @Contract("null, _, _ -> false")
    public static <T, Z> boolean has(@Nullable PersistentDataViewHolder holder, PersistentDataType<T, Z> dataType, NamespacedKey key) {
        if (holder == null) {
            return false;
        }

        return holder.getPersistentDataContainer().has(key, dataType);
    }

    @Contract("null, _, _, !null -> !null; null, _, _, null -> null")
    @Nullable
    public static <T, Z> Z getOrDefault(@Nullable PersistentDataViewHolder holder, PersistentDataType<T, Z> dataType, NamespacedKey key, @Nullable Z defaultValue) {
        Z value = get(holder, dataType, key);
        return value == null ? defaultValue : value;
    }
}
