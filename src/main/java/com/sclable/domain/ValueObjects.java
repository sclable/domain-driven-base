package com.sclable.domain;

import com.sclable.domain.exception.DomainException;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.sclable.domain.ConstraintValidations.constraint;
import static com.sclable.domain.ConstraintValidations.notNull;

public abstract class ValueObjects<E extends ValueObject> extends DomainModel
    implements Serializable {

  private final Set<E> entities = new LinkedHashSet<>();

  public Set<E> getAll() {
    return entities;
  }

  public void addAll(Set<E> entities) {
    if (entities != null) entities.forEach(this::add);
  }

  public void addAll(List<E> entities) {
    if (entities != null) entities.forEach(this::add);
  }

  public Stream<E> stream() {
    return entities.stream();
  }

  public void add(E entity) {
    ensure(constraint("entity", entity, notNull()));

    if (contains(entity)) {
      throw new DomainException(
          DomainException.ERR.CAN_NOT_ADD_EXISTING_ENTITY.parameters(
              DomainException.Parameter.of("entity", entity)));
    }

    entities.add(entity);
  }

  public void remove(E entity) {
    ensure(constraint("entity", entity, notNull()));

    if (!contains(entity)) {
      throw new DomainException(
          DomainException.ERR.CAN_NOT_REMOVE_UNKNOWN_ENTITY.parameters(
              DomainException.Parameter.of("entity", entity)));
    }

    entities.remove(entity);
  }

  public boolean isEmpty() {
    return entities.isEmpty();
  }

  public boolean containsBy(@NotNull Predicate<E> predicate) {
    return stream().anyMatch(predicate);
  }

  public void clear() {
    entities.clear();
  }

  public boolean contains(E entity) {
    return entity != null && entities.contains(entity);
  }

  public Optional<E> find(@NotNull E entity) {
    return find(e -> e.equals(entity));
  }

  public Optional<E> find(@NotNull Predicate<E> criteria) {
    return entities.stream().filter(criteria).findFirst();
  }

  public int size() {
    return entities.size();
  }

  public Optional<E> last() {
    E last = null;
    for (E e : entities) last = e;
    return Optional.ofNullable(last);
  }

  public Optional<E> first() {
    for (E e : entities) return Optional.ofNullable(e);
    return Optional.empty();
  }

  public boolean equals(ValueObjects<E> other) {
    if (other == null || entities.size() != other.size()) {
      return false;
    }

    return entities.containsAll(other.getAll());
  }
}
