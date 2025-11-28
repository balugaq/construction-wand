package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.WandUtil;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonBlockInteractor;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
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
public class BuildingWand extends PylonItem implements Wand, PylonBlockInteractor {
    private final int limitBlocks = getOrThrow("limit-blocks", ConfigAdapter.INT);
    private final boolean blockStrict = getOrThrow("block-strict", ConfigAdapter.BOOLEAN);
    private final boolean opOnly = getOrThrow("op-only", ConfigAdapter.BOOLEAN);
    private final boolean allowHandlePylonBlock = getOrThrow("allow-handle-pylon-block", ConfigAdapter.BOOLEAN);
    private final int durability = getOrThrow("durability", ConfigAdapter.INT);
    private final int cooldownTicks = getOrThrow("cooldown-ticks", ConfigAdapter.INT);

    public BuildingWand(ItemStack stack) {
        super(stack);
        if (durability > 0) {
            stack.setData(DataComponentTypes.MAX_DAMAGE, durability);
        } else {
            stack.unsetData(DataComponentTypes.MAX_DAMAGE);
        }
    }

    @Override
    public void onUsedToClickBlock(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        WandUtil.placeBlocks(ConstructionWandPlugin.getInstance(), event.getHand(), event.getPlayer(), this);
    }

    @Override
    public List<PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("range", UnitFormat.BLOCKS.format(getLimitBlocks()))
        );
    }
}
