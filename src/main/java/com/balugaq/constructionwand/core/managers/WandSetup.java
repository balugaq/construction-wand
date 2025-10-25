package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.api.items.FillWand;
import com.balugaq.constructionwand.utils.KeyUtil;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.0
 */
@NullMarked
public class WandSetup implements IManager {
    public static final SimpleStaticGuidePage MAIN;

    static {
        MAIN = new SimpleStaticGuidePage(key("construction-wand"), Material.BLAZE_ROD);
    }

    public static NamespacedKey key(String key) {
        return KeyUtil.newKey(key);
    }

    public static void registerWand(
            Class<? extends PylonItem> clazz,
            NamespacedKey key,
            Material material) {
        ItemStack item = ItemStackBuilder.pylon(material, key).build();
        PylonItem.register(clazz, item);
        MAIN.addItem(item);
    }

    public static void registerBuildingWand(
            NamespacedKey key,
            Material material) {
        registerWand(
                BuildingWand.class,
                key,
                material
        );
    }

    public static void registerBreakingWand(
            @NotNull NamespacedKey key,
            @NotNull Material material) {
        registerWand(
                BreakingWand.class,
                key,
                material
        );
    }

    public static void registerFillWand(
            NamespacedKey key,
            Material material) {
        registerWand(
                FillWand.class,
                key,
                material
        );

        PylonItem.fromStack(ItemStackBuilder.pylon(material, key).build());
    }

    @Override
    public void setup() {
        PylonGuide.getRootPage().addPage(MAIN);

        registerBuildingWand(
                key("building-wand-common"),
                Material.STONE_SWORD
        );
        registerBuildingWand(
                key("building-wand-rare"),
                Material.IRON_SWORD
        );
        registerBuildingWand(
                key("building-wand-epic"),
                Material.DIAMOND_SWORD
        );

        registerBuildingWand(
                key("building-wand-block-strict-common"),
                Material.STONE_SWORD
        );

        registerBuildingWand(
                key("building-wand-block-strict-rare"),
                Material.IRON_SWORD
        );

        registerBuildingWand(
                key("building-wand-block-strict-epic"),
                Material.DIAMOND_SWORD
        );

        registerBuildingWand(
                key("building-wand-cheat"),
                Material.NETHERITE_SWORD
        );

        registerBuildingWand(
                key("building-wand-block-strict-cheat"),
                Material.NETHERITE_SWORD
        );

        registerBreakingWand(
                key("breaking-wand-common"),
                Material.GOLDEN_SWORD
        );

        registerBreakingWand(
                key("breaking-wand-rare"),
                Material.GOLDEN_SWORD
        );

        registerBreakingWand(
                key("breaking-wand-epic"),
                Material.GOLDEN_SWORD
        );

        registerBreakingWand(
                key("breaking-wand-cheat"),
                Material.NETHERITE_SWORD
        );

        registerFillWand(
                key("fill-wand-common"),
                Material.STONE_SWORD
        );

        registerFillWand(
                key("fill-wand-rare"),
                Material.DIAMOND_SWORD
        );

        registerFillWand(
                key("fill-wand-epic"),
                Material.NETHERITE_SWORD
        );

        registerFillWand(
                key("fill-wand-cheat"),
                Material.NETHERITE_SWORD
        );
    }

    @Override
    public void shutdown() {
    }
}
