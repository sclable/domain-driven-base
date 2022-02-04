package com.sclable.domain;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class AbstractDomainObject<ID extends EntityId, Message extends DomainEventMessage>
    extends DomainConstraints implements DomainObject<ID, Message> {

  public AbstractDomainObject(@NotNull ID id) {
    this.id = id;
  }

  protected @NotNull ID id;

  private transient DomainEventCollector<Message> collector;

  public @NotNull ID getId() {
    return id;
  }

  public @NotNull UUID getIdAsUUID() {
    return getId().getId();
  }

  @Override
  public void addDomainEvent(@NotNull Message message) {
    getCollector().add(message);
  }

  @Override
  public @NotNull List<Message> getDomainEvents() {
    return getCollector().getEvents();
  }

  @Override
  public void clearDomainEvents() {
    collector.clear();
  }

  private DomainEventCollector<Message> getCollector() {
    if (collector == null) {
      collector = new DomainEventCollector<>();
    }

    return collector;
  }
}
