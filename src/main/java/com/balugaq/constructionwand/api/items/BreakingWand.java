package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.WandUtil;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonBlockInteractor;
import lombok.Getter;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class BreakingWand extends PylonItem implements Wand, PylonBlockInteractor {
    private final int limitBlocks = getOrThrow("limit-blocks", ConfigAdapter.INT);
    private final boolean blockStrict = getOrThrow("block-strict", ConfigAdapter.BOOLEAN);
    private final boolean opOnly = getOrThrow("op-only", ConfigAdapter.BOOLEAN);

    public BreakingWand(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToClickBlock(@NotNull PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        WandUtil.breakBlocks(ConstructionWandPlugin.getInstance(), event, isDisabled(), this.limitBlocks, this.blockStrict, this.opOnly);
    }

    @Override
    public @NotNull List<PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("range", getLimitBlocks())
        );
    }
}
