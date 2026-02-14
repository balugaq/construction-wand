package com.balugaq.constructionwand.utils;

import io.github.pylonmc.rebar.i18n.RebarArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author balugaq
 * @since 1.0
 */
@NullMarked
public class Messages {
    public static final String PREFIX = "pylon.constructionwand.message.";

    public static final Component NOT_SET_START_LOCATION = Component.translatable(PREFIX + "common.not-set-start-location");
    public static final Component NOT_SET_END_LOCATION = Component.translatable(PREFIX + "common.not-set-end-location");
    public static final Component NOT_SET_ITEM = Component.translatable(PREFIX + "common.not-set-material");
    public static final Component DIFFERENT_WORLDS = Component.translatable(PREFIX + "common.locations-in-different-worlds");
    public static final Component TOO_MANY_BLOCKS = Component.translatable(PREFIX + "common.too-many-blocks");
    public static final Component INVALID_BLOCK = Component.translatable(PREFIX + "common.invalid-block");
    public static final Component DISABLED_BLOCK = Component.translatable(PREFIX + "common.disabled-block");

    // arg: %start-location% - Location
    public static final String KEY_SET_START_LOCATION = PREFIX + "common.set-start-location";
    // arg: %end-location% - Location
    public static final String KEY_SET_END_LOCATION = PREFIX + "common.set-end-location";

    // arg: %start-location% - Location
    // arg: %total% - Total selected blocks
    public static final String KEY_SET_START_LOCATION_WITH_RANGE = PREFIX + "common.set-start-location-with-range";
    // arg: %end-location% - Location
    // arg: %total% - Total selected blocks
    public static final String KEY_SET_END_LOCATION_WITH_RANGE = PREFIX + "common.set-end-location-with-range";

    // arg: %material% - Translated material
    public static final String KEY_SET_ITEM = PREFIX + "common.set-material";

    // arg: %blocks% - The number of blocks filled
    public static final String KEY_FILLED_BLOCKS = PREFIX + "fill-wand.filled-blocks";

    // arg: %start-location% - Location
    public static final String KEY_START_LOCATION = PREFIX + "common.start-location";

    // arg: %end-location% - Location
    public static final String KEY_END_LOCATION = PREFIX + "common.end-location";

    // arg: %material% - Translated material
    public static final String KEY_NO_ENOUGH_ITEMS = PREFIX + "common.no-enough-items";

    public static Component arguments(@Nullable Locale locale, String translationKey, Object... args) {
        List<RebarArgument> pargs = new ArrayList<>();
        for (int i = 0; i <= args.length / 2; i += 2) {
            String argkey = args[i].toString();
            Object object = args[i + 1];
            switch (object) {
                case ComponentLike componentLike -> pargs.add(RebarArgument.of(argkey, componentLike));
                case String string -> pargs.add(RebarArgument.of(argkey, string));
                case Integer integer -> pargs.add(RebarArgument.of(argkey, integer));
                case Long longValue -> pargs.add(RebarArgument.of(argkey, longValue));
                case Double doubleValue -> pargs.add(RebarArgument.of(argkey, doubleValue));
                case Float floatValue -> pargs.add(RebarArgument.of(argkey, floatValue));
                case Boolean booleanValue -> pargs.add(RebarArgument.of(argkey, booleanValue));
                case Character character -> pargs.add(RebarArgument.of(argkey, character));
                case Number number -> pargs.add(RebarArgument.of(argkey, number.intValue()));
                default -> {
                }
            }
        }
        if (locale != null) {
            return GlobalTranslator.render(Component.translatable(translationKey, pargs), locale);
        } else {
            return Component.translatable(translationKey, pargs);
        }
    }
}
