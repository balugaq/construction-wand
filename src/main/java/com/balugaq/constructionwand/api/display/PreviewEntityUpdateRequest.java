package com.balugaq.constructionwand.api.display;

import com.balugaq.constructionwand.api.DisplayType;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@Getter
@NullMarked
public sealed abstract class PreviewEntityUpdateRequest permits PreviewEntityUpdateRequest.Add, PreviewEntityUpdateRequest.Remove {
    private final Player player;
    private final DisplayGroup group;
    private final Location location;

    public abstract void execute();

    public PreviewEntityUpdateRequest(Player player, DisplayGroup group, Location location) {
        this.player = player;
        this.group = group;
        this.location = location;
    }

    /**
     * @author balugaq
     */
    @Getter
    @NullMarked
    public static non-sealed class Add extends PreviewEntityUpdateRequest {
        private final DisplayType displayType;
        private final Material material;
        private final BlockFace originFacing;

        public Add(Player player, DisplayGroup group, Location location, DisplayType displayType, Material material, BlockFace originFacing) {
            super(player, group, location);
            this.displayType = displayType;
            this.material = material;
            this.originFacing = originFacing;
        }

        @Override
        public void execute() {
            var manager = ConstructionWandPlugin.getInstance().getDisplayManager();
            var group = manager.getDisplays().get(getPlayer().getUniqueId());
            if (group == null) return;
            manager.addDisplay(
                    group,
                    getLocation(),
                    getDisplayType(),
                    getMaterial(),
                    getOriginFacing()
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    public static non-sealed class Remove extends PreviewEntityUpdateRequest {
        public Remove(Player player, DisplayGroup group, Location location) {
            super(player, group, location);
        }

        @Override
        public void execute() {
            var manager = ConstructionWandPlugin.getInstance().getDisplayManager();
            var group = manager.getDisplays().get(getPlayer().getUniqueId());
            if (group == null) return;
            manager.removeDisplay(
                    group,
                    getLocation()
            );
        }
    }
}
