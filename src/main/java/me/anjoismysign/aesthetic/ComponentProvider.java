package me.anjoismysign.aesthetic;

import org.jetbrains.annotations.NotNull;

public interface ComponentProvider<T> {
  @NotNull
  T provide();
}
