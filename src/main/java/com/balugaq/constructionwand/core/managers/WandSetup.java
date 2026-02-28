package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.api.items.BreakingWand;
import com.balugaq.constructionwand.api.items.BuildingWand;
import com.balugaq.constructionwand.api.items.FillWand;
import com.balugaq.constructionwand.api.items.Wand;
import com.balugaq.constructionwand.utils.KeyUtil;
import io.github.pylonmc.rebar.config.Settings;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.content.guide.RebarGuide;
import io.github.pylonmc.rebar.guide.button.PageButton;
import io.github.pylonmc.rebar.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
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

    public static <T extends RebarItem & Wand> void registerWand(
            Class<T> clazz,
            @KeyPattern.Value String key,
            Material material) {
        NamespacedKey nKey = KeyUtil.newKey(key);
        ItemStackBuilder builder = ItemStackBuilder.rebar(material, nKey);
        int durability = Settings.get(nKey).get("durability", ConfigAdapter.INTEGER, -1);
        if (durability > 0) {
            builder.durability(durability);
        }
        ItemStack item = builder.build();
        RebarItem.register(clazz, item);
        MAIN_PAGE.addItem(item);
    }

    public static void registerBuildingWand(
            @Subst("type") String key,
            Material material) {
        registerWand(
                BuildingWand.class,
                "building-wand-" + key,
                material
        );
    }

    public static void registerBreakingWand(
            @Subst("type") String key,
            Material material) {
        registerWand(
                BreakingWand.class,
                "breaking-wand-" + key,
                material
        );
    }

    private void registerFillWand(
            @Subst("type") String key,
            Material material) {
        registerWand(
                FillWand.class,
                "fill-wand-" + key,
                material
        );
    }

    @Override
    public void setup() {
        RebarGuide.getRootPage().addButton(MAIN_BUTTON);

        registerBuildingWand("common", Material.STONE_SWORD);
        registerBuildingWand("rare", Material.IRON_SWORD);
        registerBuildingWand("epic", Material.GOLDEN_SWORD);
        registerBuildingWand("cheat", Material.NETHERITE_SWORD);

        registerBuildingWand("block-strict-common", Material.STONE_SWORD);
        registerBuildingWand("block-strict-rare", Material.IRON_SWORD);
        registerBuildingWand("block-strict-epic", Material.GOLDEN_SWORD);
        registerBuildingWand("block-strict-cheat", Material.NETHERITE_SWORD);

        registerBreakingWand("common", Material.STONE_SWORD);
        registerBreakingWand("rare", Material.IRON_SWORD);
        registerBreakingWand("epic", Material.GOLDEN_SWORD);
        registerBreakingWand("cheat", Material.NETHERITE_SWORD);
        
        registerFillWand("common", Material.STONE_SWORD);
        registerFillWand("rare", Material.IRON_SWORD);
        registerFillWand("epic", Material.GOLDEN_SWORD);
        registerFillWand("cheat", Material.NETHERITE_SWORD);
    }

    @Override
    public void shutdown() {
    }
}
