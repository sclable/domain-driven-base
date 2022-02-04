package com.sclable.domain;

import java.util.UUID;

public abstract class AggregateId extends EntityId {
  public AggregateId() {
    super();
  }

  public AggregateId(UUID id) {
    super(id);
  }
}
