package com.sclable.domain;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface AggregateRepository<ID extends AggregateId, AR extends AggregateRoot<ID>> {
  @NotNull
  AR save(@NotNull AR aggregateRoot);

  @NotNull
  AR save(@NotNull AR aggregateRoot, AggregateRoot<?> related);

  @NotNull
  List<AR> saveAll(@NotNull List<AR> aggregateRoots);

  @NotNull
  List<AR> saveMany(@NotNull List<AR> aggregateRoots, AggregateRoot<?> related);

  @NotNull
  List<AR> saveMany(@NotNull List<AR> aggregateRoots, AggregateId relatedId);

  @NotNull
  Optional<AR> find(@NotNull ID id);

  List<AR> findMany(List<ID> ids);

  @NotNull
  List<AR> findAll();

  void delete(@NotNull AR aggregateRoot);

  void delete(@NotNull ID id);

  Long count();
}
