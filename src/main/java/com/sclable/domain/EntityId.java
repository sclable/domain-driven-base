package com.sclable.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

import static com.sclable.domain.ConstraintValidations.constraint;
import static com.sclable.domain.ConstraintValidations.notNull;

@Getter
@EqualsAndHashCode(callSuper = false)
public abstract class EntityId extends ValueObject implements Identifiable<UUID> {
  private final UUID id;

  protected EntityId() {
    this.id = UUID.randomUUID();
  }

  protected EntityId(UUID id) {
    ensure(constraint("id", id, notNull()));

    this.id = id;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(id=" + id + ")";
  }
}
