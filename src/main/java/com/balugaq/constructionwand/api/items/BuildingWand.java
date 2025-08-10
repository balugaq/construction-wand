package com.balugaq.constructionwand.api.items;

import com.balugaq.constructionwand.api.enums.Interaction;
import com.balugaq.constructionwand.utils.PermissionUtil;
import com.balugaq.constructionwand.utils.WandUtil;
import com.balugaq.constructionwand.utils.WorldUtils;
import com.destroystokyo.paper.MaterialTags;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Getter
public class BuildingWand extends PylonItem implements Wand, PylonInteractor {
    private final int limitBlocks;
    private final boolean blockStrict;
    private final boolean opOnly;

    public BuildingWand(@NotNull ItemStack stack, int limitBlocks, boolean blockStrict, boolean opOnly) {
        super(stack);
        this.limitBlocks = limitBlocks;
        this.blockStrict = blockStrict;
        this.opOnly = opOnly;
    }

    @NotNull
    private static BlockFace getBlockFaceAsCartesian(@NotNull BlockFace originalFacing) {
        // Seems here's a bug, but it works fine...
        BlockFace lookingFacing = originalFacing.getOppositeFace();
        if (!originalFacing.isCartesian()) {
            switch (originalFacing) {
                case NORTH_EAST, NORTH_WEST, NORTH_NORTH_EAST, NORTH_NORTH_WEST -> lookingFacing = BlockFace.NORTH;
                case SOUTH_EAST, SOUTH_WEST, SOUTH_SOUTH_EAST, SOUTH_SOUTH_WEST -> lookingFacing = BlockFace.SOUTH;
                case EAST_NORTH_EAST, EAST_SOUTH_EAST -> lookingFacing = BlockFace.EAST;
                case WEST_NORTH_WEST, WEST_SOUTH_WEST -> lookingFacing = BlockFace.WEST;
                default -> {
                }
            }
        }
        return lookingFacing;
    }

    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        if (opOnly && !player.isOp()) {
            return;
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if (isDisabled()) {
            return;
        }

        Block lookingAtBlock = player.getTargetBlockExact(6, FluidCollisionMode.NEVER);
        if (lookingAtBlock == null || lookingAtBlock.getType() == Material.AIR) {
            return;
        }

        Material material = lookingAtBlock.getType();
        if (isDisabledMaterial(material)) {
            return;
        }

        int playerHas = 0;
        if (player.getGameMode() == GameMode.CREATIVE) {
            playerHas = 4096;
        } else {
            ItemStack target = new ItemStack(material, 1);
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }

                if (itemStack.isSimilar(target)) {
                    int count = itemStack.getAmount();
                    playerHas += count;
                }

                if (playerHas >= limitBlocks) {
                    break;
                }
            }
        }

        if (playerHas <= 0) {
            return;
        }

        BlockFace originalFacing = player.getTargetBlockFace(6, FluidCollisionMode.NEVER);
        if (originalFacing == null) {
            return;
        }

        BlockFace lookingFacing = getBlockFaceAsCartesian(originalFacing);

        ItemStack itemInHand = new ItemStack(material, 1);
        ItemStack item = player.getInventory().getItemInMainHand();
        Set<Location> buildingLocations = WandUtil.getBuildingLocations(player, Math.min(limitBlocks, playerHas), getAxis(item), blockStrict);

        int consumed = 0;

        Set<Block> blocks = new HashSet<>();
        for (Location location : buildingLocations) {
            Block block = location.getBlock();
            if (block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.LAVA) {
                BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(
                        block,
                        block.getState(),
                        block.getRelative(lookingFacing.getOppositeFace()),
                        itemInHand,
                        player,
                        PermissionUtil.hasPermission(player, location, Interaction.PLACE_BLOCK),
                        EquipmentSlot.HAND
                );
                Bukkit.getPluginManager().callEvent(blockPlaceEvent);
                if (!blockPlaceEvent.isCancelled()) {
                    blocks.add(block);
                    consumed += 1;
                }
            }
        }

        // I don't know why, but it must be run later, or it will create PlayerInteractEvent AGAIN!
        Bukkit.getScheduler().runTaskLater(getAddon().getJavaPlugin(), () -> {
            for (Block block : blocks) {
                if (block == null) {
                    continue;
                }

                if (copyStateAble(material)) {
                    WorldUtils.copyBlockState(lookingAtBlock.getState(), block);
                } else {
                    block.setType(material);
                }
                block.getState().update(true, true);
            }
        }, 1);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (consumed > 0) {
            player.getInventory().removeItem(new ItemStack(material, consumed));
        }
    }

    public boolean copyStateAble(@NotNull Material material) {
        return // Items that be allowed to copy state
                MaterialTags.FENCE_GATES.isTagged(material)
                        || material.name().endsWith("_SLAB")
                        || material.name().endsWith("_STAIRS")
                        || material.name().endsWith("_TRAPDOOR")
                        || material.name().endsWith("_HEAD")
                        || material.name().endsWith("_LOG")
                        || material == Material.END_ROD
                        || material.name().endsWith("LIGHTNING_ROD")
                        || material.name().endsWith("CHAIN")
                        || material.name().endsWith("_BARS")
                        || material == Material.DAYLIGHT_DETECTOR
                        || material == Material.ENDER_CHEST
                        || material == Material.NOTE_BLOCK
                        || material == Material.REDSTONE_ORE
                        || material == Material.DEEPSLATE_REDSTONE_ORE
                        || material.name().endsWith("_WALL");
    }

    public boolean isDisabledMaterial(@NotNull Material material) {
        if (// Items that can store items
                MaterialTags.SHULKER_BOXES.isTagged(material)
                        || (material.name().endsWith("CHEST") && material != Material.ENDER_CHEST)
                        || material == Material.BARREL
                        || material == Material.LECTERN
                        || material == Material.DISPENSER
                        || material == Material.DROPPER
                        || material == Material.HOPPER
                        || material == Material.VAULT
                        || material.name().endsWith("_SHELF")
                        || material == Material.SUSPICIOUS_SAND
                        || material == Material.SUSPICIOUS_GRAVEL

                        // Items that will take two blocks
                        || MaterialTags.BEDS.isTagged(material)
                        || MaterialTags.DOORS.isTagged(material)
                        || material == Material.TALL_GRASS
                        || material == Material.LARGE_FERN
                        || material == Material.TALL_SEAGRASS
                        || material == Material.SUNFLOWER
                        || material == Material.LILAC
                        || material == Material.ROSE_BUSH
                        || material == Material.PEONY
                        || material == Material.PITCHER_PLANT
                        || material == Material.PISTON_HEAD
                        || material == Material.PISTON
                        || material == Material.STICKY_PISTON

                        // Items that can place much same block in a location
                        || material.name().endsWith("CANDLE")
                        || material == Material.SEA_PICKLE
                        || material == Material.TURTLE_EGG
                        || material == materialValueOf("FROGSPAWN")

                        // Items that can't be placed in a location
                        || material.isAir()
                        || !material.isBlock()

                        // Items that is invalid / no permission
                        || material == Material.END_PORTAL_FRAME
                        || material == Material.BEDROCK
                        || material == Material.COMMAND_BLOCK
                        || material == Material.CHAIN_COMMAND_BLOCK
                        || material == Material.REPEATING_COMMAND_BLOCK
                        || material == Material.STRUCTURE_VOID
                        || material == Material.STRUCTURE_BLOCK
                        || material == Material.JIGSAW
                        || material == Material.BARRIER
                        || material == Material.LIGHT
                        || material == Material.SPAWNER
                        || material == Material.TRIAL_SPAWNER
                        || material == Material.CHORUS_FLOWER
                        || material == Material.NETHER_WART
                        || material == Material.CAVE_VINES
                        || material == Material.CAVE_VINES_PLANT
                        || material == Material.FROSTED_ICE
                        || material == Material.WATER_CAULDRON
                        || material == Material.LAVA_CAULDRON
                        || material == Material.POWDER_SNOW_CAULDRON
                        || material.name().startsWith("POTTED_")
                        || material == Material.FIRE
                        || material == Material.SOUL_FIRE
                        || material == Material.END_PORTAL
                        || material == Material.END_GATEWAY
                        || material == Material.NETHER_PORTAL
                        || material == Material.BUBBLE_COLUMN
                        || material == Material.POWDER_SNOW
                        || material == Material.MUSHROOM_STEM

                        // Items that has inventory
                        || material == Material.CRAFTING_TABLE
                        || material == Material.STONECUTTER
                        || material == Material.CARTOGRAPHY_TABLE
                        || material == Material.SMITHING_TABLE
                        || material == Material.GRINDSTONE
                        || material == Material.LOOM
                        || material == Material.FURNACE
                        || material == Material.SMOKER
                        || material == Material.BLAST_FURNACE
                        || material == Material.CAMPFIRE
                        || material == Material.SOUL_CAMPFIRE
                        || material == Material.ANVIL
                        || material == Material.CHIPPED_ANVIL
                        || material == Material.DAMAGED_ANVIL
                        || material == Material.COMPOSTER
                        || material == Material.JUKEBOX
                        || material == Material.ENCHANTING_TABLE
                        || material == Material.BREWING_STAND
                        || material == Material.CAULDRON
                        || material == Material.BEACON
                        || material == Material.BEE_NEST
                        || material == Material.BEEHIVE
                        || material == Material.FLOWER_POT
                        || material == Material.DECORATED_POT
                        || material == Material.CHISELED_BOOKSHELF
                        || MaterialTags.SIGNS.isTagged(material)
                        || material == Material.CRAFTER

                        // Items that have different types
                        || material == Material.PLAYER_HEAD
                        || material == Material.PLAYER_WALL_HEAD
                        || material.name().endsWith("CAKE")
                        | material.name().endsWith("_BUTTON")
                        || material == Material.TRIPWIRE
                        || material == Material.CREAKING_HEART

                        // Needs side block
                        || material == Material.POINTED_DRIPSTONE
                        || material.name().endsWith("_BANNER")
                        || material == Material.LEVER
                        || material.name().endsWith("TORCH")
                        || material.name().endsWith("LANTERN")
                        || material == Material.LADDER
                        || material == Material.REPEATER
                        || material == Material.COMPARATOR
                        || material == Material.VINE
                        || material == Material.GLOW_LICHEN
                        || material == Material.SCULK_VEIN
                        || material == Material.BELL
                        || material == Material.TRIPWIRE_HOOK
                        || material.name().endsWith("_RAIL")
                        || material.name().endsWith("_CORAL")
                        || material.name().endsWith("_CORAL_FAN")
                        || material.name().endsWith("_CARPET")
                        || material == Material.HANGING_ROOTS
                        || material == Material.REDSTONE_WIRE
                        || material == Material.BIG_DRIPLEAF_STEM
                        || material == Material.CHORUS_PLANT
                        || material == Material.DRAGON_EGG
                        || material == Material.SNOW
                        || material.name().endsWith("_PRESSURE_PLATE")
                        || material == Material.SMALL_AMETHYST_BUD
                        || material == Material.MEDIUM_AMETHYST_BUD
                        || material == Material.LARGE_AMETHYST_BUD
                        || material == Material.AMETHYST_CLUSTER
                        || material.name().endsWith("_SAPLING")
                        || material == Material.AZALEA
                        || material == Material.FLOWERING_AZALEA
                        || material == Material.BROWN_MUSHROOM
                        || material == Material.RED_MUSHROOM
                        || material == Material.CRIMSON_FUNGUS
                        || material == Material.WARPED_FUNGUS
                        || material == Material.SHORT_GRASS
                        || material == Material.FERN
                        || material == Material.DEAD_BUSH
                        || material == Material.DANDELION
                        || material == Material.POPPY
                        || material == Material.BLUE_ORCHID
                        || material == Material.ALLIUM
                        || material == Material.AZURE_BLUET
                        || material == Material.RED_TULIP
                        || material == Material.ORANGE_TULIP
                        || material == Material.WHITE_TULIP
                        || material == Material.PINK_TULIP
                        || material == Material.OXEYE_DAISY
                        || material == Material.CORNFLOWER
                        || material == Material.LILY_OF_THE_VALLEY
                        || material == Material.TORCHFLOWER
                        || material == Material.WITHER_ROSE
                        || material == Material.PINK_PETALS
                        || material == Material.SPORE_BLOSSOM
                        || material == Material.BAMBOO
                        || material == Material.SUGAR_CANE
                        || material == Material.CACTUS
                        || material == Material.CRIMSON_ROOTS
                        || material == Material.WARPED_ROOTS
                        || material == Material.NETHER_SPROUTS
                        || material == Material.WEEPING_VINES
                        || material == Material.TWISTING_VINES
                        || material == Material.WEEPING_VINES_PLANT
                        || material == Material.TWISTING_VINES_PLANT
                        || material == Material.COCOA
                        || material == Material.SWEET_BERRY_BUSH
                        || material == Material.TORCHFLOWER_CROP
                        || material == Material.WHEAT
                        || material == Material.MELON_STEM
                        || material == Material.PUMPKIN_STEM
                        || material == Material.POTATOES
                        || material == Material.CARROTS
                        || material == Material.BEETROOTS
                        || material == Material.KELP
                        || material == Material.KELP_PLANT
                        || material == Material.SEAGRASS
                        || material == Material.LILY_PAD
                        || material == Material.OPEN_EYEBLOSSOM
                        || material == Material.CLOSED_EYEBLOSSOM
                        || material == Material.PALE_HANGING_MOSS
                        || material == Material.MANGROVE_PROPAGULE
                        || material == materialValueOf("WILDFLOWERS")
                        || material == materialValueOf("LEAF_LITTER")
                        || material.name().endsWith("_WALL_FAN")
                        || material == Material.RESIN_CLUMP
        ) {
            return true;
        }

        return false;
    }

    @NotNull
    private Material materialValueOf(@NotNull String name) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Material.AIR;
        }
    }
}
