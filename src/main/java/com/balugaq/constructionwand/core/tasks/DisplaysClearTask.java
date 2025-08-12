package com.balugaq.constructionwand.core.tasks;

import com.balugaq.constructionwand.api.events.PrepareBreakingEvent;
import com.balugaq.constructionwand.api.events.PrepareBuildingEvent;
import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.core.managers.DisplayManager;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.WandUtil;
import io.github.pylonmc.pylon.core.item.PylonItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class DisplaysClearTask implements Task {
    @Getter
    private final DisplayManager manager;

    public DisplaysClearTask() {
        manager = ConstructionWandPlugin.getInstance().getDisplayManager();
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param bukkitTask the input argument
     */
    @Override
    public void accept(@NotNull BukkitTask bukkitTask) {
        if (!manager.isRunning()) {
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
