package com.balugaq.constructionwand.utils;

import com.balugaq.constructionwand.core.managers.ConfigManager;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author balugaq
 */
@SuppressWarnings({"unused", "CallToPrintStackTrace"})
@UtilityClass
@NullMarked
public class Debug {
    private static @Nullable JavaPlugin plugin = null;
    private static final Queue<String> buffer = new ConcurrentLinkedQueue<>();

    public static void init() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ConstructionWandPlugin.getInstance(), () -> {
            while (!buffer.isEmpty()) {
                log0(buffer.poll());
            }
        }, 1, 1);
    }

    public static void debug(Object object) {
        debug(object.toString());
    }

    public static void debug(String message) {
        if (ConfigManager.debug()) {
            log("[Debug] " + message);
        }
    }

    public static void sendMessage(Player player, @Nullable Object... objects) {
        sendMessage(player, Arrays.toString(objects));
    }

    public static void sendMessage(Player player, @Nullable Object object) {
        if (object == null) {
            sendMessage(player, "null");
            return;
        }
        sendMessage(player, object.toString());
    }

    public static void sendMessages(@NotNull Player player, String @NotNull ... messages) {
        for (String message : messages) {
            sendMessage(player, message);
        }
    }

    public static void sendMessage(@NotNull Player player, String message) {
        player.sendMessage("[" + getPlugin().getLogger().getName() + "]" + message);
    }

    public static void dumpStack() {
        Thread.dumpStack();
    }

    public static void log(Object object) {
        log(object.toString());
    }

    public static void log(String message) {
        buffer.add(message);
    }

    public static void log(Throwable e) {
        e.printStackTrace();
    }

    public static void log() {
        log("");
    }

    public static void severe(Throwable e) {
        log(e);
    }

    public static void severe(String message) {
        log("&c[SEVERE] " + message);
    }

    public static void severe(Object object) {
        severe(object.toString());
    }

    private static void log0(String message) {
        getPlugin().getLogger().info(message);
    }

    public static JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = ConstructionWandPlugin.getInstance();
        }
        return plugin;
    }
}
