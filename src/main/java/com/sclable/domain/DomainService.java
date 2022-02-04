package com.sclable.domain;

import com.sclable.domain.exception.DomainException.DomainError;
import com.sclable.domain.exception.DomainException.ERR;

import java.util.Optional;
import java.util.function.Function;

public abstract class DomainService {
  @SafeVarargs
  protected static void ensure(Function<String, Optional<DomainError>>... constraints) {
    var dsc = new DomainServiceConstraints();
    dsc.validate(constraints).raise(ERR.DOMAIN_SERVICE_CONSTRAINTS_VIOLATED);
  }

  private static class DomainServiceConstraints extends DomainConstraints {}
}
