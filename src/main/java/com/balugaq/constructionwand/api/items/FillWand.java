package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.KeyUtil;
import com.balugaq.constructionwand.utils.Messages;
import com.balugaq.constructionwand.utils.PersistentUtil;
import com.balugaq.constructionwand.utils.WandUtil;
import com.balugaq.constructionwand.utils.WorldUtils;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.i18n.RebarArgument;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.RebarItemSchema;
import io.github.pylonmc.rebar.item.base.RebarInteractor;
import io.github.pylonmc.rebar.registry.RebarRegistry;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 * @since 1.0
 */
@Getter
@NullMarked
public class FillWand extends RebarItem implements Wand, RebarInteractor {
    public static final NamespacedKey START_LOCATION_KEY = KeyUtil.newKey("start-location");
    public static final NamespacedKey END_LOCATION_KEY = KeyUtil.newKey("end-location");
    public static final NamespacedKey ITEM_KEY = KeyUtil.newKey("item");
    private final int limitBlocks = getOrThrow("limit-blocks", ConfigAdapter.INTEGER);
    private final boolean opOnly = getOrThrow("op-only", ConfigAdapter.BOOLEAN);
    private final boolean allowHandleRebarBlock = getOrThrow("allow-handle-pylon-block", ConfigAdapter.BOOLEAN);
    private final int durability = getOrThrow("durability", ConfigAdapter.INTEGER);
    private final int cooldownTicks = getOrThrow("cooldown-ticks", ConfigAdapter.INTEGER);

    public FillWand(ItemStack stack) {
        super(stack);
    }

    @SuppressWarnings({"UnstableApiUsage", "DataFlowIssue"})
    public static void resolveWandLore(Player player, ItemStack wand) {
        ItemLore.Builder lore = ItemLore.lore();
        lore.addLines(wand.lore().stream().limit(5).toList());
        PersistentDataContainerView view = wand.getPersistentDataContainer();

        String startLocation = view.get(START_LOCATION_KEY, PersistentDataType.STRING);
        String endLocation = view.get(END_LOCATION_KEY, PersistentDataType.STRING);
        String material = view.get(ITEM_KEY, PersistentDataType.STRING);

        if (startLocation != null || endLocation != null || material != null) {
            lore.addLine(Component.text(" "));
        }

        if (startLocation != null) {
            lore.addLine(Messages.arguments(
                    player.locale(),
                    Messages.KEY_START_LOCATION,
                    "start-location",
                    humanizeLoc(resolveStr2Loc(startLocation))
            ));
        }

        if (endLocation != null) {
            lore.addLine(Messages.arguments(
                    player.locale(),
                    Messages.KEY_END_LOCATION,
                    "end-location",
                    humanizeLoc(resolveStr2Loc(endLocation))
            ));
        }

        wand.setData(DataComponentTypes.LORE, lore);
    }

    @Contract("null -> null")
    @Nullable
    public static ItemStack resolveStr2item(@Nullable String str) {
        if (str == null) return null;
        if (str.startsWith("minecraft:")) {
            Material material = Material.matchMaterial(str);
            if (material == null) return null;
            return ItemStack.of(material);
        }

        NamespacedKey key = NamespacedKey.fromString(str);
        if (key == null) return null;

        RebarItemSchema schema = RebarRegistry.ITEMS.get(key);
        if (schema != null) return schema.getItemStack();
        return null;
    }

    @Contract("null -> null; !null -> !null")
    @Nullable
    public static String resolveItem2str(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        RebarItem item = RebarItem.fromStack(itemStack);
        if (item != null) {
            NamespacedKey block = item.getRebarBlock();
            if (block != null) {
                if (RebarRegistry.BLOCKS.get(block) != null) {
                    return item.getKey().toString();
                }
            }
        }

        return "minecraft:" + itemStack.getType().name().toLowerCase();
    }

    // str: world_name;x;y;z
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    @Contract("null -> null; !null -> !null")
    @Nullable
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
    public static String resolveLoc2str(Location location) {
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    public static String humanizeLoc(Location location) {
        return "X: " + location.getBlockX() + " | Y: " + location.getBlockY() + " | Z: " + location.getBlockZ();
    }

    public static Component humanizeItemName(Player player, ItemStack itemStack) {
        RebarItem item = RebarItem.fromStack(itemStack);
        if (item != null) {
            return GlobalTranslator.render(itemStack.displayName(), player.locale());
        } else {
            return GlobalTranslator.render(Component.translatable("block.minecraft." + resolveItem2str(itemStack).toLowerCase()), player.locale());
        }
    }

    @Override
    public void onUsedToClick(PlayerInteractEvent event, EventPriority priority) {
        if (isDisabled()) {
            return;
        }

        if (!event.getAction().isRightClick()) {
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
        boolean clickOnBlock =
                event.hasBlock() && (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK);
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
                // Set end location
                Location startLocation = resolveStr2Loc(PersistentUtil.get(wand, PersistentDataType.STRING, END_LOCATION_KEY));
                if (startLocation != null) {
                    player.sendMessage(Messages.arguments(
                            player.locale(),
                            Messages.KEY_SET_START_LOCATION_WITH_RANGE,
                            "start-location",
                            humanizeLoc(location),
                            "total",
                            WorldUtils.totalBlocks(location, startLocation)
                    ));
                } else {
                    player.sendMessage(Messages.arguments(
                            player.locale(),
                            Messages.KEY_SET_START_LOCATION,
                            "start-location",
                            humanizeLoc(location)
                    ));
                }
                PersistentUtil.set(wand, PersistentDataType.STRING, START_LOCATION_KEY, resolveLoc2str(location));
                resolveWandLore(player, wand);
                return;
            }

            if (rightClick) {
                if (shift) {
                    // Set material
                    ItemStack item = WandUtil.getItemType(this, block);
                    if (item == null) {
                        player.sendMessage(Messages.INVALID_BLOCK);
                        return;
                    }

                    Material material = item.getType();
                    if (material.isAir() || !material.isItem()) {
                        player.sendMessage(Messages.INVALID_BLOCK);
                        return;
                    }

                    if (WandUtil.isItemDisabledToBuild(item)) {
                        player.sendMessage(Messages.DISABLED_BLOCK);
                        return;
                    }

                    player.sendMessage(Messages.arguments(
                            player.locale(),
                            Messages.KEY_SET_ITEM,
                            "item",
                            humanizeItemName(player, item)
                    ));
                    PersistentUtil.set(wand, PersistentDataType.STRING, ITEM_KEY, resolveItem2str(item));
                    resolveWandLore(player, wand);
                } else {
                    // Set end location
                    Location endLocation = resolveStr2Loc(PersistentUtil.get(wand, PersistentDataType.STRING, START_LOCATION_KEY));
                    if (endLocation != null) {
                        player.sendMessage(Messages.arguments(
                                player.locale(),
                                Messages.KEY_SET_END_LOCATION_WITH_RANGE,
                                "end-location",
                                humanizeLoc(location),
                                "total",
                                WorldUtils.totalBlocks(endLocation, location)
                        ));
                    } else {
                        player.sendMessage(Messages.arguments(
                                player.locale(),
                                Messages.KEY_SET_END_LOCATION,
                                "end-location",
                                humanizeLoc(location)
                        ));
                    }
                    PersistentUtil.set(wand, PersistentDataType.STRING, END_LOCATION_KEY, resolveLoc2str(location));
                    resolveWandLore(player, wand);
                }
            }
        } else {
            // click on air
            if (rightClick) {
                Location startLocation = resolveStr2Loc(PersistentUtil.get(wand, PersistentDataType.STRING, START_LOCATION_KEY));
                Location endLocation = resolveStr2Loc(PersistentUtil.get(wand, PersistentDataType.STRING, END_LOCATION_KEY));
                if (startLocation == null) {
                    player.sendMessage(Messages.NOT_SET_START_LOCATION);
                    return;
                }

                if (endLocation == null) {
                    player.sendMessage(Messages.NOT_SET_END_LOCATION);
                    return;
                }

                if (startLocation.getWorld() != endLocation.getWorld()) {
                    player.sendMessage(Messages.DIFFERENT_WORLDS);
                    return;
                }

                if (WorldUtils.totalBlocks(startLocation, endLocation) > getHandleableBlocks()) {
                    player.sendMessage(Messages.TOO_MANY_BLOCKS);
                    return;
                }

                ItemStack item = resolveStr2item(PersistentUtil.get(wand, PersistentDataType.STRING, ITEM_KEY));
                if (item == null) {
                    player.sendMessage(Messages.NOT_SET_ITEM);
                    return;
                }

                int filled = WandUtil.fillBlocks(ConstructionWandPlugin.getInstance(), event, startLocation, endLocation, item, getLimitBlocks());
                if (filled == 0) {
                    player.sendMessage(Messages.arguments(
                            player.locale(),
                            Messages.KEY_NO_ENOUGH_ITEMS,
                            "material",
                            humanizeItemName(player, item)
                    ));
                }
                player.sendMessage(Messages.arguments(
                        player.locale(),
                        Messages.KEY_FILLED_BLOCKS,
                        "blocks",
                        filled
                ));
                resolveWandLore(player, wand);
            }
        }
    }

    @Override
    public List<RebarArgument> getPlaceholders() {
        return List.of(
                RebarArgument.of("range", UnitFormat.BLOCKS.format(getLimitBlocks()))
        );
    }

    @Override
    public boolean isBlockStrict() {
        return false;
    }
}
