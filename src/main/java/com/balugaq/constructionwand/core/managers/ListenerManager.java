package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.core.listeners.PlayerInteractListener;
import com.balugaq.constructionwand.core.listeners.PrepareBreakingListener;
import com.balugaq.constructionwand.core.listeners.PrepareBuildingListener;
import com.balugaq.constructionwand.core.listeners.WandModeSwitchListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager implements IManager {
    private final @NotNull JavaPlugin plugin;
    private final List<Listener> listeners = new ArrayList<>();

    public ListenerManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        listeners.add(new PrepareBuildingListener());
        listeners.add(new PrepareBreakingListener());
        listeners.add(new PlayerInteractListener());
        listeners.add(new WandModeSwitchListener());
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void shutdown() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }

        listeners.clear();
    }
}
