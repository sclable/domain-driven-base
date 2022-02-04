package com.sclable.domain;

import com.sclable.domain.exception.DomainException;
import com.sclable.domain.exception.DomainException.ERR;
import lombok.Getter;
import lombok.Setter;

import java.lang.invoke.MethodHandles;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class AggregateRoot<ID extends AggregateId> extends Entity<ID> {

  @Getter @Setter private long version;

  public AggregateRoot(ID id) {
    super(id);
  }

  public AggregateRoot(ID id, long version) {
    super(id);

    this.version = version;
  }

  public AggregateRoot(ID id, long version, boolean copied, ID originalId) {
    super(id, copied, originalId);

    this.version = version;
  }

  public static Supplier<DomainException> notFound(UUID id) {
    return () ->
        new DomainException(
            ERR.AGGREGATE_NOT_FOUND.parameters(
                DomainException.Parameter.of(
                    "class", MethodHandles.lookup().lookupClass().getSimpleName()),
                DomainException.Parameter.of("id", id.toString())));
  }

  public static <ID extends EntityId> Supplier<DomainException> notFoundBy(String name, ID id) {
    return notFoundBy(name, id.getId());
  }

  public static Supplier<DomainException> notFoundBy(String name, UUID id) {
    return () ->
        new DomainException(
            ERR.AGGREGATE_NOT_FOUND_BY.parameters(
                DomainException.Parameter.of(
                    "class", MethodHandles.lookup().lookupClass().getSimpleName()),
                DomainException.Parameter.of("name", name),
                DomainException.Parameter.of("id", id.toString())));
  }
}
