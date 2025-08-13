package com.balugaq.constructionwand.api.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Data
public class Pair<A, B> {
    private final @NotNull A first;
    private final @NotNull B second;
}
