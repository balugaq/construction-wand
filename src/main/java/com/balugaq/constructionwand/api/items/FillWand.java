package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.KeyUtil;
import com.balugaq.constructionwand.utils.Messages;
import com.balugaq.constructionwand.utils.PersistentUtil;
import com.balugaq.constructionwand.utils.WandUtil;
import com.balugaq.constructionwand.utils.WorldUtils;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class FillWand extends PylonItem implements Wand, PylonInteractor {
    public static final NamespacedKey LOC1_KEY = KeyUtil.newKey("loc1");
    public static final NamespacedKey LOC2_KEY = KeyUtil.newKey("loc2");
    public static final NamespacedKey MATERIAL_KEY = KeyUtil.newKey("material");
    public static final Map<NamespacedKey, List<Component>> originLore = new HashMap<>();
    private final int limitBlocks = getOrThrow("limit-blocks", ConfigAdapter.INT);
    private final boolean opOnly = getOrThrow("op-only", ConfigAdapter.BOOLEAN);

    @SuppressWarnings({"UnstableApiUsage", "DataFlowIssue"})
    public FillWand(@NotNull ItemStack stack) {
        super(stack);

        if (!originLore.containsKey(getKey())) {
            originLore.put(getKey(), stack.getData(DataComponentTypes.LORE).lines());
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void resolveWandLore(@NotNull Player player, @NotNull NamespacedKey key, @NotNull ItemStack wand) {
        ItemLore.Builder lore = ItemLore.lore();
        lore.addLines(originLore.get(key));
        PersistentDataContainerView view = wand.getPersistentDataContainer();

        String loc1 = view.get(LOC1_KEY, PersistentDataType.STRING);
        String loc2 = view.get(LOC2_KEY, PersistentDataType.STRING);
        String material = view.get(MATERIAL_KEY, PersistentDataType.STRING);

        if (loc1 != null || loc2 != null || material != null) {
            lore.addLine(Component.text(" "));
        }

        if (loc1 != null) {
            lore.addLine(Messages.argsWithed(
                    player.locale(),
                    Messages.KEY_LOC1,
                    "loc1",
                    humanizeLoc(resolveStr2Loc(loc1))
            ));
        }

        if (loc2 != null) {
            lore.addLine(Messages.argsWithed(
                    player.locale(),
                    Messages.KEY_LOC2,
                    "loc2",
                    humanizeLoc(resolveStr2Loc(loc2))
            ));
        }

        if (material != null) {
            lore.addLine(Messages.argsWithed(
                    player.locale(),
                    Messages.KEY_MATERIAL,
                    "material",
                    humanizeMaterialName(resolveStr2material(material))
            ));
        }

        wand.setData(DataComponentTypes.LORE, lore);
    }

    @Contract("null -> null")
    public static Material resolveStr2material(@Nullable String str) {
        if (str == null) return null;
        return Material.matchMaterial(str);
    }

    @Contract("null -> null; !null -> !null")
    public static String resolveMaterial2str(@Nullable Material material) {
        if (material == null) {
            return null;
        }

        return material.name();
    }

    // str: world_name;x;y;z
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    @Contract("null -> null; !null -> !null")
    public static Location resolveStr2Loc(@Nullable String str) {
        if (str == null) return null;

        String[] parts = str.split(";");
        if (parts.length != 4) {
            return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }

        try {
            World world = Bukkit.getWorld(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid location string", e);
        }
    }

    // str: world_name;x;y;z
    @NotNull
    public static String resolveLoc2str(@NotNull Location location) {
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    @NotNull
    public static String humanizeLoc(@NotNull Location location) {
        return "X: " + location.getBlockX() + " | Y: " + location.getBlockY() + " | Z: " + location.getBlockZ();
    }

    @NotNull
    public static TranslatableComponent humanizeMaterialName(@NotNull Material material) {
        return Component.translatable("block.minecraft." + resolveMaterial2str(material).toLowerCase());
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        if (isDisabled()) {
            return;
        }

        Player player = event.getPlayer();
        if (isOpOnly() && !player.isOp()) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack wand = event.getItem();
        if (wand == null) {
            return;
        }

        Action action = event.getAction();
        boolean clickOnBlock = event.hasBlock() && (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK);
        boolean leftClick = action.isLeftClick();
        boolean rightClick = action.isRightClick();
        boolean shift = event.getPlayer().isSneaking();

        if (clickOnBlock) {
            Block block = event.getClickedBlock();
            if (block == null) {
                return;
            }

            Location location = block.getLocation();
            if (leftClick) {
                // Set loc1
                Location loc2 = resolveStr2Loc(PersistentUtil.get(wand, PersistentDataType.STRING, LOC2_KEY));
                if (loc2 != null) {
                    player.sendMessage(Messages.argsWithed(
                            player.locale(),
                            Messages.KEY_SET_LOC1_WITH_RANGE,
                            "loc1",
                            humanizeLoc(location),
                            "total",
                            WorldUtils.totalBlocks(location, loc2)
                    ));
                } else {
                    player.sendMessage(Messages.argsWithed(
                            player.locale(),
                            Messages.KEY_SET_LOC1,
                            "loc1",
                            humanizeLoc(location)
                    ));
                }
                PersistentUtil.set(wand, PersistentDataType.STRING, LOC1_KEY, resolveLoc2str(location));
                resolveWandLore(player, getKey(), wand);
                return;
            }

            if (rightClick) {
                if (shift) {
                    // Set material
                    Material material = block.getType();
                    if (material.isAir() || !material.isItem()) {
                        player.sendMessage(Messages.INVALID_BLOCK);
                        return;
                    }

                    if (WandUtil.isMaterialDisabledToBuild(material)) {
                        player.sendMessage(Messages.DISABLED_BLOCK);
                        return;
                    }

                    player.sendMessage(Messages.argsWithed(
                            player.locale(),
                            Messages.KEY_SET_MATERIAL,
                            "material",
                            humanizeMaterialName(material)
                    ));
                    PersistentUtil.set(wand, PersistentDataType.STRING, MATERIAL_KEY, resolveMaterial2str(material));
                    resolveWandLore(player, getKey(), wand);
                } else {
                    // Set loc2
                    Location loc1 = resolveStr2Loc(PersistentUtil.get(wand, PersistentDataType.STRING, LOC1_KEY));
                    if (loc1 != null) {
                        player.sendMessage(Messages.argsWithed(
                                player.locale(),
                                Messages.KEY_SET_LOC2_WITH_RANGE,
                                "loc2",
                                humanizeLoc(location),
                                "total",
                                WorldUtils.totalBlocks(loc1, location)
                        ));
                    } else {
                        player.sendMessage(Messages.argsWithed(
                                player.locale(),
                                Messages.KEY_SET_LOC2,
                                "loc2",
                                humanizeLoc(location)
                        ));
                    }
                    PersistentUtil.set(wand, PersistentDataType.STRING, LOC2_KEY, resolveLoc2str(location));
                    resolveWandLore(player, getKey(), wand);
                }
            }
        } else {
            // click on air
            if (rightClick) {
                Location loc1 = resolveStr2Loc(PersistentUtil.get(wand, PersistentDataType.STRING, LOC1_KEY));
                Location loc2 = resolveStr2Loc(PersistentUtil.get(wand, PersistentDataType.STRING, LOC2_KEY));
                if (loc1 == null) {
                    player.sendMessage(Messages.NOT_SET_LOC1);
                    return;
                }

                if (loc2 == null) {
                    player.sendMessage(Messages.NOT_SET_LOC2);
                    return;
                }

                if (loc1.getWorld() != loc2.getWorld()) {
                    player.sendMessage(Messages.DIFFERENT_WORLDS);
                    return;
                }

                if (WorldUtils.totalBlocks(loc1, loc2) > getLimitBlocks()) {
                    player.sendMessage(Messages.TOO_MANY_BLOCKS);
                    return;
                }

                Material material = resolveStr2material(PersistentUtil.get(wand, PersistentDataType.STRING, MATERIAL_KEY));
                if (material == null) {
                    player.sendMessage(Messages.NOT_SET_MATERIAL);
                    return;
                }

                int filled = WandUtil.fillBlocks(ConstructionWandPlugin.getInstance(), event, loc1, loc2, material, getLimitBlocks());
                if (filled == 0) {
                    player.sendMessage(Messages.argsWithed(
                            player.locale(),
                            Messages.KEY_NO_ENOUGH_ITEMS,
                            "material",
                            humanizeMaterialName(material)
                    ));
                }
                player.sendMessage(Messages.argsWithed(
                        player.locale(),
                        Messages.KEY_FILLED_BLOCKS,
                        "blocks",
                        filled
                ));
                resolveWandLore(player, getKey(), wand);
            }
        }
    }

    @Override
    public @NotNull List<PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("range", getLimitBlocks())
        );
    }

    @Override
    public boolean isBlockStrict() {
        return false;
    }
}
