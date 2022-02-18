package com.sclable.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DomainExceptionTest {
  @Test
  public void errParametersAreNotOverwritten() {
    var firstError =
        DomainException.ERR.AGGREGATE_NOT_FOUND_BY.parameters(
            DomainException.Parameter.of("name", "name"));

    DomainException.ERR.AGGREGATE_NOT_FOUND_BY.parameters(
        DomainException.Parameter.of("name", "otherName"));

    Assertions.assertEquals("name", firstError.getParameters().get(0).getValue());
  }

  @Test
  public void parametersCanBeAddedAfterInitialisation() {
    var error = DomainException.ERR.AGGREGATE_NOT_FOUND_BY.toError();

    error.addParameters(DomainException.Parameter.of("name", "name"));

    Assertions.assertEquals("name", error.getParameters().get(0).getValue());
  }

  @Test
  public void parametersAreInterpolatedInMessage() {
    String someClass = "some class";
    String someName = "some name";
    String someId = "some id";

    var error =
        DomainException.ERR.AGGREGATE_NOT_FOUND_BY.parameters(
            DomainException.Parameter.of("class", someClass),
            DomainException.Parameter.of("name", someName),
            DomainException.Parameter.of("id", someId));

    assertTrue(
        Stream.of(
                error.getMessage().contains(someClass),
                error.getMessage().contains(someName),
                error.getMessage().contains(someId))
            .allMatch((result) -> result));
  }
}
