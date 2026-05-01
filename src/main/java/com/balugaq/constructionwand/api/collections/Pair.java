package com.balugaq.constructionwand.api.collections;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public record Pair<A, B>(A first, B second) {
}
