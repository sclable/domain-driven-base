package com.sclable.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public interface EventCollector<E extends Serializable> {
  @NotNull
  List<E> getEvents();

  void add(@NotNull @Valid E event);

  void clear();

  Serializable getSourceEntity();
}
