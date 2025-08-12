package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.KeyUtil;
import com.balugaq.constructionwand.utils.PersistentUtil;
import com.balugaq.constructionwand.utils.WandUtil;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class FillWand extends PylonItem implements Wand {
    public static final NamespacedKey LOC1_KEY = KeyUtil.newKey("loc1");
    public static final NamespacedKey LOC2_KEY = KeyUtil.newKey("loc2");
    public static final NamespacedKey MATERIAL_KEY = KeyUtil.newKey("material");
    private final int limitBlocks = getOrThrow("limit-blocks", Integer.class);
    private final boolean opOnly = getOrThrow("op-only", Boolean.class);
    private final long cooldown = getOrThrow("cooldown", Integer.class);

    public FillWand(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToClickBlock(@NotNull PlayerInteractEvent event) {
        if (isCooldowning(event.getPlayer())) {
            return;
        }

        ItemStack wand = event.getItem();
        Action action = event.getAction();
        boolean clickOnBlock = event.hasBlock() && (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK);
        boolean leftClick = action.isLeftClick();
        boolean rightClick = action.isRightClick();
        boolean shift = event.getPlayer().isSneaking();

        // 左键onBlock loc1
        // 右键onBlock loc2
        // 右键空气 fill
        // shift右键onBlock 设置material
        Player player = event.getPlayer();
        if (clickOnBlock) {
            Block block = event.getClickedBlock();
            if (block == null) {
                player.sendMessage("Block is invalid");
                return;
            }

            Location location = block.getLocation();
            if (leftClick) {
                // Set loc1
                player.sendMessage(Messages.SET_LOC1);
                PersistentUtil.set(wand, LOC1_KEY, resolveLoc2pdc(location));
                resolveWandLore(wand);
                return;
            }

            if (rightClick) {
                // Set loc2
                player.sendMessage(Messages.SET_LOC2);
                PersistentUtil.set(wand, LOC2_KEY, resolveLoc2pdc(location));
                resolveWandLore(wand);
                return;
            }
        }

        WandUtil.fillBlocks(ConstructionWandPlugin.getInstance(), event, loc1, loc2, isDisabled(), this.limitBlocks, this.opOnly);
    }

    @Override
    public @NotNull List<PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("limit_blocks", getLimitBlocks())
        );
    }

    @Override
    public boolean isBlockStrict() {
        return false;
    }
}
