package com.balugaq.constructionwand.api.display;

import com.balugaq.constructionwand.api.DisplayType;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * @author balugaq
 */
@Getter
public sealed abstract class PreviewUpdateRequest permits PreviewUpdateRequest.Add, PreviewUpdateRequest.Remove{
    private final Player player;
    private final DisplayGroup group;
    private final Location location;

    public abstract void execute();

    public PreviewUpdateRequest(Player player, DisplayGroup group, Location location) {
        this.player = player;
        this.group = group;
        this.location = location;
    }

    /**
     * @author balugaq
     */
    @Getter
    public static non-sealed class Add extends PreviewUpdateRequest {
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
            ConstructionWandPlugin.getInstance().getDisplayManager().addDisplay(
                    getGroup(),
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
    public static non-sealed class Remove extends PreviewUpdateRequest {
        public Remove(Player player, DisplayGroup group, Location location) {
            super(player, group, location);
        }

        @Override
        public void execute() {
            ConstructionWandPlugin.getInstance().getDisplayManager().removeDisplay(
                    getGroup(),
                    getLocation()
            );
        }
    }
}
