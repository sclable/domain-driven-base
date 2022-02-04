package com.sclable.domain;

import java.io.Serializable;

public interface Event<T extends Enum<T>, P extends Serializable> {
  T getEventType();

  P getPayload();
}
