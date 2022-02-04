package com.sclable.domain;

import com.sclable.domain.exception.DomainException;
import com.sclable.domain.exception.DomainException.Parameter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

import static com.sclable.domain.ConstraintValidations.constraint;
import static com.sclable.domain.ConstraintValidations.notNull;
import static com.sclable.domain.exception.DomainException.ERR.ENTITY_ID_ALREADY_EXISTS;
import static com.sclable.domain.exception.DomainException.ERR.ENTITY_ID_NOT_FOUND;

@ToString
public abstract class EntityIds<ID extends EntityId> extends DomainModel {
  protected Set<ID> ids;

  public EntityIds() {
    initialize();
  }

  protected void initialize() {
    ids = new HashSet<>();
  }

  public Set<ID> getAll() {
    return ids;
  }

  public void addAll(Set<ID> ids) {
    if (ids != null) {
      ids.forEach(this::add);
    }
  }

  public void add(ID id) {
    if (contains(id)) {
      throw new DomainException(
          ENTITY_ID_ALREADY_EXISTS.parameters(
              Parameter.of("name", id.getClass().getSimpleName()), Parameter.of("id", id)));
    }

    ids.add(id);
  }

  public void remove(ID id) {
    if (!contains(id)) {
      throw new DomainException(
          ENTITY_ID_NOT_FOUND.parameters(
              Parameter.of("name", id.getClass().getSimpleName()), Parameter.of("id", id)));
    }

    ids.remove(id);
  }

  public void clear() {
    ids.clear();
  }

  public int size() {
    return ids.size();
  }

  public boolean isEmpty() {
    return ids.isEmpty();
  }

  public boolean contains(ID id) {
    ensure(constraint("id", id, notNull()));

    return ids.contains(id);
  }
}
