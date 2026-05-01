package com.balugaq.constructionwand.core.tasks;

import com.balugaq.constructionwand.core.managers.DisplayManager;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public class DisplaysClearTask extends BukkitRunnable {
    @Override
    public void run() {
        if (!ConstructionWandPlugin.getInstance().getDisplayManager().isRunning()) {
            this.cancel();
            return;
        }

        for (World world : Bukkit.getWorlds()) {
            world.getEntities().forEach(entity -> {
                if (entity instanceof Display display) {
                    List<MetadataValue> metadata = display.getMetadata(ConstructionWandPlugin.getInstance().getName());
                    if (!metadata.isEmpty()) {
                        if (metadata.getFirst().asBoolean()) {
                            display.remove();
                        }
                    }
                }
            });
        }
    }
}
