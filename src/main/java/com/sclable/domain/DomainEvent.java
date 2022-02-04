package com.sclable.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class DomainEvent<ET extends Enum<ET>, DO extends Serializable> implements Event<ET, DO> {
  private final ET eventType;
  private final DO payload;
}
