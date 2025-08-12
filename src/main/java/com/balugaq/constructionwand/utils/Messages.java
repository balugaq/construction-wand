package com.balugaq.constructionwand.utils;

import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

public class Messages {
    public static final String PREFIX = "pylon.constructionwand.message.";

    public static final Component NOT_SET_LOC1 = Component.translatable(PREFIX + "common.not-set-loc1");
    public static final Component NOT_SET_LOC2 = Component.translatable(PREFIX + "common.not-set-loc2");
    public static final Component NOT_SET_MATERIAL = Component.translatable(PREFIX + "common.not-set-material");
    public static final Component DIFFERENT_WORLDS = Component.translatable(PREFIX + "common.locations-in-different-worlds");
    public static final Component TOO_MANY_BLOCKS = Component.translatable(PREFIX + "common.too-many-blocks");
    public static final Component INVALID_BLOCK = Component.translatable(PREFIX + "common.invalid-block");
    public static final Component DISABLED_BLOCK = Component.translatable(PREFIX + "common.disabled-block");

    // arg: %loc% - Humanized loc: ex: "X:1 | Y:1 | Z:1"
    public static final String KEY_SET_LOC1 = PREFIX + "common.set-loc1";
    // arg: %loc% - Humanized loc: ex: "X:1 | Y:1 | Z:1"
    public static final String KEY_SET_LOC2 = PREFIX + "common.set-loc2";

    // arg: %material% - Translated material: ex: "Dirt" in English, "泥土" in Chinese
    public static final String KEY_SET_MATERIAL = PREFIX + "common.set-material";

    // arg: %blocks% - The number of blocks filled: ex: "1"
    public static final String KEY_FILLED_BLOCKS = PREFIX + "fill-wand.filled-blocks";

    // arg: %loc% - Humanized loc
    public static final String KEY_LOC1 = PREFIX + "common.loc1";

    // arg: %loc% - Humanized loc
    public static final String KEY_LOC2 = PREFIX + "common.loc2";

    // arg: %material% - Translated material
    public static final String KEY_MATERIAL = PREFIX + "common.material";

    // arg: %material% - Translated material
    public static final String KEY_NO_ENOUGH_ITEMS = PREFIX + "common.no-enough-items";

    @NotNull
    public static TranslatableComponent argsWithed(@NotNull String key, @NotNull String argName, int arg) {
        return Component.translatable(key, PylonArgument.of(argName, arg));
    }

    @NotNull
    public static TranslatableComponent argsWithed(@NotNull String key, @NotNull String argName, float arg) {
        return Component.translatable(key, PylonArgument.of(argName, arg));
    }

    @NotNull
    public static TranslatableComponent argsWithed(@NotNull String key, @NotNull String argName, long arg) {
        return Component.translatable(key, PylonArgument.of(argName, arg));
    }

    @NotNull
    public static TranslatableComponent argsWithed(@NotNull String key, @NotNull String argName, double arg) {
        return Component.translatable(key, PylonArgument.of(argName, arg));
    }

    @NotNull
    public static TranslatableComponent argsWithed(@NotNull String key, @NotNull String argName, @NotNull String arg) {
        return Component.translatable(key, PylonArgument.of(argName, arg));
    }

    @NotNull
    public static TranslatableComponent argsWithed(@NotNull String key, @NotNull String argName, @NotNull ComponentLike arg) {
        return Component.translatable(key, PylonArgument.of(argName, arg));
    }

    @NotNull
    public static TranslatableComponent argsWithed(@NotNull String key, @NotNull String argName, boolean arg) {
        return Component.translatable(key, PylonArgument.of(argName, arg));
    }

    @NotNull
    public static TranslatableComponent argsWithed(@NotNull String key, @NotNull String argName, char arg) {
        return Component.translatable(key, PylonArgument.of(argName, arg));
    }
}
