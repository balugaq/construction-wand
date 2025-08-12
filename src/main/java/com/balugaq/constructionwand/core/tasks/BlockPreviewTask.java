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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BlockPreviewTask implements Task {
    @Getter
    private final DisplayManager manager;

    public BlockPreviewTask() {
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

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                return;
            }
            UUID uuid = player.getUniqueId();
            Block block = player.getTargetBlockExact(6, FluidCollisionMode.NEVER);
            if (block == null || block.getType().isAir()) {
                lookingAt.remove(uuid);
                killDisplays(uuid);
                continue;
            }

            BlockFace originalFacing = player.getTargetBlockFace(6, FluidCollisionMode.NEVER);
            if (originalFacing == null) {
                lookingAt.remove(uuid);
                lookingFaces.remove(uuid);
                killDisplays(uuid);
                continue;
            }

            Location location = block.getLocation();
            if (!lookingAt.containsKey(uuid) || !lookingAt.get(uuid).equals(location) || !lookingFaces.containsKey(uuid) || !lookingFaces.get(uuid).equals(originalFacing)) {
                killDisplays(uuid);
                lookingAt.put(uuid, location);
                lookingFaces.put(uuid, originalFacing);

                PylonItem wandLike = PylonItem.fromStack(player.getInventory().getItemInMainHand());
                if (wandLike instanceof BuildingWand buildingWand) {
                    if (buildingWand.isDisabled()) {
                        continue;
                    }

                    if (WandUtil.isMaterialDisabledToBuild(block.getType())) {
                        continue;
                    }

                    PrepareBuildingEvent event = new PrepareBuildingEvent(player, buildingWand, block);
                    Bukkit.getPluginManager().callEvent(event);
                }

                if (wandLike instanceof BreakingWand breakingWand) {
                    if (breakingWand.isDisabled()) {
                        continue;
                    }

                    if (WandUtil.isMaterialDisabledToBreak(block.getType())) {
                        continue;
                    }

                    PrepareBreakingEvent event = new PrepareBreakingEvent(player, breakingWand, block);
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        }
    }
}
