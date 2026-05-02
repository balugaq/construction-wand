package com.balugaq.constructionwand.api.display;

import com.balugaq.constructionwand.api.DisplayType;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * @author balugaq
 */
public record PreviewUpdateRequest(Player player, Set<Location> coming, Material material, DisplayType displayType) {
    public void execute() {
        ConstructionWandPlugin.getInstance().getDisplayManager().updateDisplays(player, coming, material, displayType);
    }
}
