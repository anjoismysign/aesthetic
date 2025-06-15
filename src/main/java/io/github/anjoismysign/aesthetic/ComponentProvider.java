package io.github.anjoismysign.aesthetic;

import org.jetbrains.annotations.NotNull;

public interface ComponentProvider<T> {
    @NotNull
    T provide();
}
