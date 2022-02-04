package com.sclable.domain;

import com.sclable.domain.exception.DomainException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.SerializationUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sclable.domain.ConstraintValidations.constraint;
import static com.sclable.domain.ConstraintValidations.notNull;
import static com.sclable.domain.exception.DomainException.ERR.ENTITY_ALREADY_EXISTS;
import static com.sclable.domain.exception.DomainException.ERR.ENTITY_NOT_FOUND;

public abstract class Entities<ID extends EntityId, ENTITY extends Entity<ID>> extends DomainModel {
  protected Set<ENTITY> entities;

  public Entities() {
    initialize();
  }

  protected void initialize() {
    entities = new HashSet<>();
  }

  public Set<ENTITY> getAll() {
    return entities;
  }

  public List<ID> getIds() {
    return entities.stream().map(Entity::getId).collect(Collectors.toList());
  }

  public Stream<ENTITY> stream() {
    return entities.stream();
  }

  public void forEach(Consumer<ENTITY> action) {
    entities.forEach(action);
  }

  public void addAll(Set<ENTITY> entities) {
    if (entities != null) {
      entities.forEach(this::add);
    }
  }

  public void add(ENTITY entity) {
    ensure(constraint("entity", entity, notNull()));

    if (contains(entity)) {
      throw new DomainException(
          ENTITY_ALREADY_EXISTS.parameters(
              DomainException.Parameter.of("name", entity.getClass().getSimpleName()),
              DomainException.Parameter.of("id", entity.getId())));
    }

    entities.add(entity);
  }

  public void remove(ENTITY entity) {
    ensure(constraint("entity", entity, notNull()));

    if (!contains(entity)) {
      throw new DomainException(
          ENTITY_NOT_FOUND.parameters(
              DomainException.Parameter.of("name", entity.getClass().getSimpleName()),
              DomainException.Parameter.of("id", entity.getId())));
    }

    entities.remove(entity);
  }

  public void remove(ID id) {
    find(id).ifPresentOrElse(this::remove, () -> Entity.notFound(id));
  }

  public boolean contains(ID id) {
    ensure(constraint("id", id, notNull()));

    return find(id).isPresent();
  }

  public boolean contains(ENTITY entity) {
    ensure(constraint("entity", entity, notNull()));

    return contains(entity.getId());
  }

  public boolean containsBy(Predicate<ENTITY> predicate) {
    ensure(constraint("predicate", predicate, notNull()));

    return stream().anyMatch(predicate);
  }

  public Optional<ENTITY> find(ID id) {
    ensure(constraint("id", id, notNull()));

    return entities.stream().filter(entity -> entity.getId().equals(id)).findFirst();
  }

  public Optional<ENTITY> find(ENTITY entity) {
    ensure(constraint("entity", entity, notNull()));

    return find(entity.getId());
  }

  public Optional<ENTITY> find(Predicate<ENTITY> predicate) {
    ensure(constraint("predicate", predicate, notNull()));

    return entities.stream().filter(predicate).findFirst();
  }

  public void clear() {
    entities.clear();
  }

  public int size() {
    return entities.size();
  }

  public boolean isEmpty() {
    return entities.isEmpty();
  }

  public boolean equals(Entities<ID, ENTITY> other) {
    if (other == null || entities.size() != other.size()) {
      return false;
    }

    return entities.containsAll(other.getAll());
  }

  @SneakyThrows
  public Entities<ID, ENTITY> copy() {
    var clone = SerializationUtils.clone(this);
    clone.clear();

    entities.forEach(entity -> clone.add(entity.copy()));

    return clone;
  }
}
