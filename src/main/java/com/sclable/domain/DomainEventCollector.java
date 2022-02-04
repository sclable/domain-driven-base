package com.sclable.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DomainEventCollector<Message extends DomainEventMessage>
    implements EventCollector<Message> {
  private List<Message> events;

  public @NotNull List<Message> getEvents() {
    if (events == null) {
      events = new ArrayList<>();
    }

    return events;
  }

  @Override
  public void add(@NotNull @Valid Message event) {
    getEvents().add(event);
  }

  @Override
  public void clear() {
    getEvents().clear();
  }

  @Override
  public Identifiable<?> getSourceEntity() {
    return null;
  }
}
