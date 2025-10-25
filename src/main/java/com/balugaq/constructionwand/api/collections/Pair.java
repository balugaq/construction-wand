package com.balugaq.constructionwand.api.collections;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.0
 */
@NullMarked
public record Pair<A, B>(A first, B second) {
}
