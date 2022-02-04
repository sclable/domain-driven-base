package com.sclable.domain;

import com.sclable.domain.exception.DomainException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class provides methods to ensure domain constraints are fulfilled.
 *
 * <p>Examples:
 *
 * <p>Throws a DomainException if propertyValue is null. By default a {@link DomainException} with
 * {@link DomainException.DomainError}.DOMAIN_CONSTRAINTS_VIOLATED is thrown. AddtionalErrors will
 * be populated with one constraint-specific ERR per failed validation.
 *
 * <pre>{@code
 * ensure(
 *   constraint("propertyName", propertyValue, notBlank())
 * )
 * }</pre>
 *
 * Validate does not throw immediately and can be chained with raise to throw a custom error
 *
 * <pre>{@code
 * validate(
 *   constraint("propertyName", propertyValue, notBlank())
 * ).raise(ERR.MY_BUSINESS_METHOD_SPECIFIC_ERROR)
 * }</pre>
 */
public abstract class DomainConstraints {
  private final List<DomainException.DomainError> constraintViolations = new ArrayList<>();

  /**
   * Domain constraint composition method that throws a {@link DomainException} if at least one
   * constraint is violated. A {@link DomainException} with {@link
   * DomainException.ERR}.DOMAIN_CONSTRAINTS_VIOLATED is thrown.
   *
   * @param constraints {@link com.sclable.domain.ConstraintValidations#constraint(String, Object,
   *     Function)} methods that return a optional ERR on validation error.
   * @throws DomainException with {@link DomainException.DomainError}.DOMAIN_CONSTRAINTS_VIOLATED as
   *     error. addtionalErrors list will be populated with one constraint-specific ERR per failed
   *     validation.
   */
  @SafeVarargs
  protected final void ensure(
      Function<String, Optional<DomainException.DomainError>>... constraints) {
    validate(constraints).raise();
  }

  /**
   * Validate performs validation checks on the passed constraint methods. If a constraint is
   * violated, the returned {@link DomainException.DomainError} is added to {@link
   * DomainConstraints#constraintViolations}.
   *
   * @param constraints functions containing validation rules that return an ERR on failure. The
   *     easiest way is use {@link com.sclable.domain.ConstraintValidations#constraint(String,
   *     Object, Function)} to create a validation function.
   * @return self to be chainable with {@link DomainConstraints#raise()}.
   */
  @SafeVarargs
  protected final DomainConstraints validate(
      Function<String, Optional<DomainException.DomainError>>... constraints) {
    Arrays.stream(constraints)
        .map(function -> function.apply(this.getClass().getSimpleName()))
        .flatMap(Optional::stream)
        .forEach(constraintViolations::add);

    return this;
  }

  /**
   * Check if constraints are fulfilled or not.
   *
   * @param constraints {@link com.sclable.domain.ConstraintValidations#constraint(String, Object,
   *     Function)} methods that return a optional ERR on validation error.
   * @return true if all constraints are valid otherwise false.
   */
  @SafeVarargs
  protected final boolean isValid(
      Function<String, Optional<DomainException.DomainError>>... constraints) {
    validate(constraints);
    return constraintViolations.isEmpty();
  }

  /**
   * Throw a {@link DomainException} with {@link
   * DomainException.DomainError}.DOMAIN_CONSTRAINTS_VIOLATED as error.
   *
   * @throws DomainException with {@link DomainException.DomainError}.DOMAIN_CONSTRAINTS_VIOLATED as
   *     error.
   */
  public final void raise() {
    raise(DomainException.ERR.DOMAIN_CONSTRAINTS_VIOLATED);
  }

  /**
   * Throw a {@link DomainException} with the passed root cause as error if {@link
   * DomainConstraints#constraintViolations} contains errors.
   *
   * @param rootCause error to throw
   * @throws DomainException with {@link DomainException.DomainError}.DOMAIN_CONSTRAINTS_VIOLATED as
   *     error. addtionalErrors list will be populated with one constraint-specific ERR per failed
   *     validation in {@link DomainConstraints#constraintViolations}.
   */
  public final void raise(DomainException.ERR rootCause) {
    if (constraintViolations.isEmpty()) {
      return;
    }

    try {

      throw new DomainException(
          rootCause.parameters(DomainException.Parameter.of("caller", getCallingMethod())),
          constraintViolations);
    } finally {
      constraintViolations.clear();
    }
  }

  private String getCallingMethod() {
    var stackTrace = Thread.currentThread().getStackTrace();
    return Stream.of(Arrays.copyOfRange(stackTrace, 1, stackTrace.length))
        .filter(element -> !element.getClassName().equals(DomainConstraints.class.getName()))
        .findFirst()
        .map(element -> element.getClassName() + "#" + element.getMethodName())
        .orElse("");
  }
}
