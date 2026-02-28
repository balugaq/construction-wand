package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.WandUtil;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.i18n.RebarArgument;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.base.RebarBlockInteractor;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 * @since 1.0
 */
@NullMarked
@Getter
public class BuildingWand extends RebarItem implements Wand, RebarBlockInteractor {
    private final int limitBlocks = getOrThrow("limit-blocks", ConfigAdapter.INTEGER);
    private final boolean blockStrict = getOrThrow("block-strict", ConfigAdapter.BOOLEAN);
    private final boolean opOnly = getOrThrow("op-only", ConfigAdapter.BOOLEAN);
    private final boolean allowHandleRebarBlock = getOrThrow("allow-handle-pylon-block", ConfigAdapter.BOOLEAN);
    private final int durability = getOrThrow("durability", ConfigAdapter.INTEGER);
    private final int cooldownTicks = getOrThrow("cooldown-ticks", ConfigAdapter.INTEGER);

    public BuildingWand(ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToClickBlock(PlayerInteractEvent event, EventPriority priority) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        WandUtil.placeBlocks(ConstructionWandPlugin.getInstance(), event.getHand(), event.getPlayer(), this);
    }

    @Override
    public List<RebarArgument> getPlaceholders() {
        return List.of(
                RebarArgument.of("range", UnitFormat.BLOCKS.format(getLimitBlocks()))
        );
    }
}
