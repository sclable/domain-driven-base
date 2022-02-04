package com.sclable.domain;

import lombok.ToString;

import java.util.LinkedHashSet;

@ToString(callSuper = true)
public abstract class OrderedEntityIds<ID extends EntityId> extends EntityIds<ID> {
  @Override
  protected void initialize() {
    ids = new LinkedHashSet<>();
  }
}
