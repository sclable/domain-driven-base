package com.sclable.domain;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface DomainObject<ID extends EntityId, DE extends DomainEventMessage>
    extends Serializable {
  @NotNull
  ID getId();

  @NotNull
  UUID getIdAsUUID();

  void addDomainEvent(@NotNull DE message);

  @NotNull
  List<DE> getDomainEvents();

  void clearDomainEvents();
}
