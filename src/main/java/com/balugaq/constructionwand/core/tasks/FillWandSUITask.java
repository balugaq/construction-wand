package com.balugaq.constructionwand.core.tasks;

import com.balugaq.constructionwand.api.items.FillWand;
import com.balugaq.constructionwand.core.managers.DisplayManager;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.Debug;
import com.balugaq.constructionwand.utils.ParticleUtil;
import com.balugaq.constructionwand.utils.WorldUtils;
import io.github.pylonmc.pylon.core.item.PylonItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FillWandSUITask extends BukkitRunnable {
    @Getter
    private final DisplayManager manager;

    public FillWandSUITask() {
        manager = ConstructionWandPlugin.getInstance().getDisplayManager();
    }

    @Override
    public void run() {
        if (!manager.isRunning()) {
            this.cancel();
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack wand = player.getInventory().getItemInMainHand();
            PylonItem wandInstance = PylonItem.fromStack(wand);
            if (!(wandInstance instanceof FillWand fw)) {
                continue;
            }

            Location loc1 = FillWand.resolveStr2Loc(wand.getPersistentDataContainer().get(FillWand.LOC1_KEY, PersistentDataType.STRING));
            Location loc2 = FillWand.resolveStr2Loc(wand.getPersistentDataContainer().get(FillWand.LOC2_KEY, PersistentDataType.STRING));
            if (loc1 == null || loc2 == null) {
                continue;
            }

            if (loc1.getWorld() != loc2.getWorld()) {
                continue;
            }
            
            if (WorldUtils.totalBlocks(loc1, loc2) > fw.getLimitBlocks()) {
                continue;
            }

            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(ConstructionWandPlugin.getInstance(), () -> ParticleUtil.drawRegionOutline(ConstructionWandPlugin.getInstance(), Particle.WAX_OFF, 0, loc1, loc2), 10);
        }
    }
}
