package com.sclable.domain;

import com.sclable.domain.exception.DomainException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import static com.sclable.domain.ConstraintValidations.constraint;
import static com.sclable.domain.ConstraintValidations.notNull;

public abstract class Entity<ID extends EntityId> extends DomainModel implements Identifiable<ID> {
  @Getter private final ID id;
  @Getter private boolean copied;
  @Getter private ID originalId;

  public Entity(ID id) {
    ensure(constraint("id", id, notNull()));

    this.id = id;
    this.copied = false;
    this.originalId = null;
  }

  public Entity(ID id, boolean copied, ID originalId) {
    ensure(constraint("id", id, notNull()));

    this.id = id;
    this.copied = copied;
    this.originalId = originalId;
  }

  @SuppressWarnings("unchecked")
  public <T extends Entity<ID>> T copy() {
    return (T) EntityCopyUtils.copy(this);
  }

  protected void markAsCopied() {
    this.copied = true;
  }

  protected void assignOriginalId(ID originalId) {
    ensure(constraint("originalId", originalId, notNull()));

    this.originalId = originalId;
  }

  @Override
  public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Entity)) {
      return false;
    }

    final var other = (Entity<?>) obj;

    return getId().equals(other.getId());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(getId());
  }

  public static <ID extends EntityId> Supplier<DomainException> notFound(ID id) {
    return notFound(id.getId());
  }

  public static Supplier<DomainException> notFound(UUID id) {
    return () ->
        new DomainException(
            DomainException.ERR.ENTITY_NOT_FOUND.parameters(
                DomainException.Parameter.of(
                    "class", MethodHandles.lookup().lookupClass().getSimpleName()),
                DomainException.Parameter.of("id", id.toString())));
  }

  public static <ID> Supplier<DomainException> notFoundBy(String name, ID id) {
    return () ->
        new DomainException(
            DomainException.ERR.ENTITY_NOT_FOUND.parameters(
                DomainException.Parameter.of(
                    "class", MethodHandles.lookup().lookupClass().getSimpleName()),
                DomainException.Parameter.of("name", name),
                DomainException.Parameter.of("id", id.toString())));
  }
}
