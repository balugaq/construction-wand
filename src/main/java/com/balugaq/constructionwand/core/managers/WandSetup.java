package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.api.items.FillWand;
import com.balugaq.constructionwand.utils.KeyUtil;
import io.github.pylonmc.rebar.content.guide.RebarGuide;
import io.github.pylonmc.rebar.guide.button.PageButton;
import io.github.pylonmc.rebar.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
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
    public static final PageButton MAIN_BUTTON;
    public static final SimpleStaticGuidePage MAIN_PAGE;

    static {
        MAIN_PAGE = new SimpleStaticGuidePage(key("construction-wand"));
        MAIN_BUTTON = new PageButton(Material.BLAZE_ROD, MAIN_PAGE);
    }

    public static NamespacedKey key(String key) {
        return KeyUtil.newKey(key);
    }

    public static void registerWand(
            Class<? extends RebarItem> clazz,
            NamespacedKey key,
            Material material) {
        ItemStack item = ItemStackBuilder.rebar(material, key).build();
        RebarItem.register(clazz, item);
        MAIN_PAGE.addItem(item);
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

        RebarItem.fromStack(ItemStackBuilder.rebar(material, key).build());
    }

    @Override
    public void setup() {
        RebarGuide.getRootPage().addButton(MAIN_BUTTON);

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
                Material.GOLDEN_SWORD
        );

        registerBuildingWand(
                key("building-wand-cheat"),
                Material.NETHERITE_SWORD
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
                Material.GOLDEN_SWORD
        );

        registerBuildingWand(
                key("building-wand-block-strict-cheat"),
                Material.NETHERITE_SWORD
        );

        registerBreakingWand(
                key("breaking-wand-common"),
                Material.STONE_SWORD
        );

        registerBreakingWand(
                key("breaking-wand-rare"),
                Material.IRON_SWORD
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
                Material.IRON_SWORD
        );

        registerFillWand(
                key("fill-wand-epic"),
                Material.GOLDEN_SWORD
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
