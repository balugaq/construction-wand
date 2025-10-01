package com.balugaq.constructionwand.api.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jspecify.annotations.NullMarked;

@AllArgsConstructor
@Data
@NullMarked
public class Pair<A, B> {
    private final A first;
    private final B second;
}
