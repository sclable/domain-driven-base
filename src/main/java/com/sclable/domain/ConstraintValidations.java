package com.sclable.domain;

import com.sclable.domain.exception.DomainException.DomainError;
import com.sclable.domain.exception.DomainException.ERR;
import com.sclable.domain.exception.DomainException.Parameter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sclable.domain.exception.DomainException.ERR.*;

/**
 * Constraint validations for single properties.
 *
 * <p>Validates that property named value to be a positive money amount:
 *
 * <pre>{@code
 * constraint("value", value, positiveMoney()))
 * }</pre>
 *
 * Validates name to be not blank, but only if it set:
 *
 * <pre>{@code
 * constraint("name", name, allowNull(notBlank())
 * }</pre>
 *
 * Validates a property named comment to be at most 100 characters long:
 *
 * <pre>{@code
 * constraint("comment", commentString, maxLength(100))
 * }</pre>
 *
 * Constraint with custom validation constraint function:
 *
 * <pre>{@code
 * constraint("custom", customProperty, (value) ->
 *    Objects.equals(value, "custom validation")
 *        ? Optional.of(ERR.CUSTOM_ERROR)
 *        : Optional.empty())
 * }</pre>
 */
public final class ConstraintValidations {

  /**
   * Does a constraint check and creates an {@link ERR} with parameters if validation fails.
   *
   * @param name The value's property name
   * @param value Value of validation
   * @param constraint validation constraint function
   * @param <X> Type of value to be validated
   * @return optional ERR
   */
  public static <X> Function<String, Optional<DomainError>> constraint(
      String name, X value, Function<X, Optional<DomainError>> constraint) {
    return className ->
        constraint
            .apply(value)
            .map(
                err -> {
                  err.addParameters(
                      Parameter.of("class", className),
                      Parameter.of("name", name),
                      Parameter.of("value", value));
                  return err;
                });
  }

  /**
   * Does a constraint check and creates an {@link ERR} with parameters if validation fails.
   *
   * @param name The value's property name
   * @param value Value of validation
   * @param constraint validation constraint function
   * @param domainError domainError to be added
   * @param <X> Type of value to be validated
   * @return optional ERR
   */
  public static <X> Function<String, Optional<DomainError>> constraint(
      String name,
      X value,
      Function<X, Optional<DomainError>> constraint,
      DomainError domainError) {
    return className ->
        constraint
            .apply(value)
            .map(
                err -> domainError.addParameters(err.getParameters().toArray(new Parameter[0]))
                .addParameters(
                    Parameter.of("class", className),
                    Parameter.of("name", name),
                    Parameter.of("value", value)));
  }

  /**
   * Does a constraint check and creates an {@link ERR} with parameters if validation fails.
   *
   * @param name The value's property name
   * @param value Value of validation
   * @param constraint validation constraint function
   * @param error error to be added
   * @param <X> Type of value to be validated
   * @return optional ERR
   */
  public static <X> Function<String, Optional<DomainError>> constraint(
      String name, X value, Function<X, Optional<DomainError>> constraint, ERR error) {
    return constraint(name, value, constraint, error.toError());
  }

  /**
   * Skip the {@code constraint} validation if {@code value} is null.
   *
   * @param constraint validation constraint function
   * @param <X> type of property to be skipped
   * @return result of actual constraint or optional empty if property is null
   */
  public static <X> Function<X, Optional<DomainError>> allowNull(
      Function<X, Optional<DomainError>> constraint) {
    return value -> Objects.nonNull(value) ? constraint.apply(value) : Optional.empty();
  }

  /**
   * Skip the {@code constraint} validation if {@code value} is null or the passed condition is met.
   *
   * @param conditionMet skip validation if true
   * @param constraint validation constraint function
   * @param <X> type of property to be skipped
   * @return result of actual constraint or optional empty if property is null
   */
  public static <X> Function<X, Optional<DomainError>> allowNullIf(
      boolean conditionMet, Function<X, Optional<DomainError>> constraint) {
    return value ->
        (Objects.nonNull(value) || !conditionMet) ? constraint.apply(value) : Optional.empty();
  }

  /**
   * Noop validator to check if value can be null.
   *
   * @return optional empty
   */
  public static <X> Function<X, Optional<DomainError>> nullable() {
    return value -> Optional.empty();
  }

  /**
   * Validates the {@code value} to be not null.
   *
   * @param <X> any object
   * @return NOT_NULL constraint violation
   */
  public static <X> Function<X, Optional<DomainError>> notNull() {
    return value -> Objects.isNull(value) ? Optional.of(NOT_NULL.toError()) : Optional.empty();
  }

  /**
   * Validates the {@code value} to equal the given literal.
   *
   * @param <X> any object
   * @return NOT_NULL constraint violation
   */
  public static <X> Function<X, Optional<DomainError>> isLiteral(X literal) {
    return nonNull(
        value ->
            literal.equals(value)
                ? Optional.empty()
                : Optional.of(LITERAL_VIOLATION.parameters(Parameter.of("literal", literal))));
  }

  /**
   * Validates the {@code value} to be null.
   *
   * @param <X> any object
   * @return IS_NULL constraint violation
   */
  public static <X> Function<X, Optional<DomainError>> isNull() {
    return value -> Objects.nonNull(value) ? Optional.of(IS_NULL.toError()) : Optional.empty();
  }

  /**
   * Validates a String not being null or blank.
   *
   * @return NOT_NULL | NOT_BLANK constraint violation
   */
  public static Function<String, Optional<DomainError>> notBlank() {
    return nonNull(
        value -> StringUtils.isBlank(value) ? Optional.of(NOT_BLANK.toError()) : Optional.empty());
  }

  /**
   * Validates a Collection not being null or empty
   *
   * @return NOT_NULL | IS_EMPTY constraint violation
   */
  public static Function<Collection<?>, Optional<DomainError>> notEmpty() {
    return nonNull(value -> value.isEmpty() ? Optional.of(IS_EMPTY.toError()) : Optional.empty());
  }

  /**
   * Validates Number to be not null and positive
   *
   * @return NOT_NULL | NOT_POSITIVE constraint violation
   */
  public static Function<Number, Optional<DomainError>> positiveNumber() {
    return nonNull(
        value -> value.doubleValue() <= 0 ? Optional.of(NOT_POSITIVE.toError()) : Optional.empty());
  }

  /**
   * Validates Number to be not null and positive or zero
   *
   * @return NOT_NULL | NOT_ZERO_OR_POSITIVE constraint violation
   */
  public static Function<Number, Optional<DomainError>> zeroOrPositiveNumber() {
    return nonNull(
        value ->
            value.doubleValue() < 0
                ? Optional.of(NOT_ZERO_OR_POSITIVE.toError())
                : Optional.empty());
  }

  /**
   * Validates if a collection does not contain the specified element
   *
   * @return NOT_NULL | COLLECTION_CONTAINS constraint violation
   */
  public static <X, Y> Function<X, Optional<DomainError>> notContains(
      Collection<Y> collection, Function<Y, X> fn) {
    return nonNull(
        value ->
            collection.stream().anyMatch(e -> fn.apply(e).equals(value))
                ? Optional.of(COLLECTION_CONTAINS.toError())
                : Optional.empty());
  }

  /**
   * Validates if a collection has distinct values only
   *
   * @return NOT_NULL | NOT_UNIQUE constraint violation
   */
  public static <X, Y> Function<Collection<X>, Optional<DomainError>> uniqueBy(
      Function<X, Y> mapper) {
    return nonNull(
        value -> {
          var uniqueValues = value.stream().map(mapper).collect(Collectors.toSet());
          return uniqueValues.size() != value.size()
              ? Optional.of(NOT_UNIQUE.toError())
              : Optional.empty();
        });
  }

  /**
   * Validates Number to be not null and negative
   *
   * @return NOT_NULL | NOT_NEGATIVE constraint violation
   */
  public static Function<Number, Optional<DomainError>> negativeNumber() {
    return nonNull(
        value -> value.doubleValue() >= 0 ? Optional.of(NOT_NEGATIVE.toError()) : Optional.empty());
  }

  /**
   * Validates Number to be not null and negative or zero
   *
   * @return NOT_NULL | NOT_ZERO_OR_NEGATIVE constraint violation
   */
  public static Function<Number, Optional<DomainError>> zeroOrNegativeNumber() {
    return nonNull(
        value ->
            value.doubleValue() > 0
                ? Optional.of(NOT_ZERO_OR_NEGATIVE.toError())
                : Optional.empty());
  }

  /**
   * Validates Period to be not null and negative
   *
   * @return NOT_NULL | NOT_NEGATIVE constraint violation
   */
  public static Function<Period, Optional<DomainError>> positiveOrZeroPeriod() {
    return nonNull(
        value -> value.isNegative() ? Optional.of(NOT_POSITIVE.toError()) : Optional.empty());
  }

  public static Function<Number, Optional<DomainError>> lessThanOrEqualToNumber(Number other) {
    return nonNull(
        value ->
            value.doubleValue() > other.doubleValue()
                ? Optional.of(NOT_LESS_THAN_OR_EQUAL.parameters(Parameter.of("other", other)))
                : Optional.empty());
  }

  /**
   * Validates a string to have at most {@code max} characters.
   *
   * @param max Maximum number of characters
   * @return STRING_OUT_OF_BOUNDS constraint violation
   */
  public static Function<String, Optional<DomainError>> maxStringLength(int max) {
    return stringLengthBetween(0, max);
  }

  /**
   * Validates a string to have a least {@code min} characters.
   *
   * @param min Minimum number of characters
   * @return STRING_OUT_OF_BOUNDS constraint violation
   */
  public static Function<String, Optional<DomainError>> minStringLength(int min) {
    return stringLengthBetween(min, Integer.MAX_VALUE);
  }

  /**
   * Validates a string to have characters between {@code min} and {@code max}.
   *
   * @param min Minimum number of characters
   * @param max Maximum number of characters
   * @return STRING_OUT_OF_BOUNDS constraint violation
   */
  public static Function<String, Optional<DomainError>> stringLengthBetween(int min, int max) {
    return nonNull(
        value ->
            (value.length() < min || value.length() > max)
                ? Optional.of(
                    STRING_OUT_OF_BOUNDS.parameters(
                        Parameter.of("min", min), Parameter.of("max", max)))
                : Optional.empty());
  }

  /**
   * Validates a number to be between {@code min} and {@code max}.
   *
   * @param min Minimum number
   * @param max Maximum number
   * @return NUMBER_OUT_OF_BOUNDS constraint violation
   */
  public static Function<Number, Optional<DomainError>> numberBetween(Number min, Number max) {
    return nonNull(
        value ->
            (value.doubleValue() < min.doubleValue() || value.doubleValue() > max.doubleValue())
                ? Optional.of(
                    NUMBER_OUT_OF_BOUNDS.parameters(
                        Parameter.of("min", min), Parameter.of("max", max)))
                : Optional.empty());
  }

  /**
   * Validates a property not being reassigned if a value is assigned already. Assigning null is
   * allowed as long as the property is unassigned (== null).
   *
   * @param current already assigned entity id to compare {@code value} to
   * @return SAME_ALREADY_ASSIGNED | ANOTHER_ALREADY_ASSIGNED constraint violations
   */
  public static <T> Function<T, Optional<DomainError>> assignableOnce(T current) {
    return value ->
        Objects.nonNull(current)
            ? current.equals(value)
                ? Optional.of(SAME_ALREADY_ASSIGNED.toError())
                : Optional.of(ANOTHER_ALREADY_ASSIGNED.parameters(Parameter.of("other", current)))
            : Optional.empty();
  }

  /**
   * Validates LocalDate to be not null and in the future
   *
   * @return NOT_NULL | NOT_IN_FUTURE constraint violation
   */
  public static Function<LocalDate, Optional<DomainError>> futureLocalDate() {
    return nonNull(
        value ->
            value.isBefore(LocalDate.now())
                ? Optional.of(NOT_IN_FUTURE.toError())
                : Optional.empty());
  }

  /**
   * Validates ZonedDateTime to be not null and today or in future
   *
   * @return NOT_NULL | TODAY_OR_IN_FUTURE constraint violation
   */
  public static Function<ZonedDateTime, Optional<DomainError>> todayOrInFuture() {
    var currentDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);

    return nonNull(
        value ->
            value.truncatedTo(ChronoUnit.DAYS).equals(currentDate) || value.isAfter(currentDate)
                ? Optional.empty()
                : Optional.of(TODAY_OR_IN_FUTURE.toError()));
  }

  /**
   * Validates LocalDate to be not null and in the past
   *
   * @return NOT_NULL | NOT_IN_PAST constraint violation
   */
  public static Function<LocalDate, Optional<DomainError>> pastLocalDate() {
    return nonNull(
        value ->
            value.isAfter(LocalDate.now()) ? Optional.of(NOT_IN_PAST.toError()) : Optional.empty());
  }

  /**
   * Validates LocalDate to be not null and today or in future
   *
   * @return NOT_NULL | TODAY_OR_IN_FUTURE constraint violation
   */
  public static Function<LocalDate, Optional<DomainError>> todayOrInFutureLocalDate() {
    var currentDate = LocalDate.now();

    return nonNull(
        value ->
            value.equals(currentDate) || value.isAfter(currentDate)
                ? Optional.empty()
                : Optional.of(TODAY_OR_IN_FUTURE.toError()));
  }

  public static Function<LocalDate, Optional<DomainError>> dateBefore(LocalDate other) {
    return nonNull(
        value ->
            other == null || other.isBefore(value)
                ? Optional.of(NOT_BEFORE.parameters(Parameter.of("other", other)))
                : Optional.empty());
  }

  public static Function<LocalDate, Optional<DomainError>> dateAfterOrEqual(LocalDate other) {
    return nonNull(
        value -> other.isAfter(value) || !value.isEqual(other)
            ? Optional.of(NOT_AFTER.parameters(Parameter.of("other", other)))
            : Optional.empty());
  }

  public static Function<LocalDate, Optional<DomainError>> dateAfter(LocalDate other) {
    return nonNull(
        value ->
            other == null || other.isAfter(value)
                ? Optional.of(NOT_AFTER.parameters(Parameter.of("other", other)))
                : Optional.empty());
  }

  public static <X> Function<X, Optional<DomainError>> inCollection(Collection<X> collection) {
    return nonNull(
        value ->
            collection.contains(value)
                ? Optional.empty()
                : Optional.of(VALUE_NOT_IN_COLLECTION.toError()));
  }

  /**
   * Null checks before calling actual constraint
   *
   * @param constraint validation constraint function
   * @param <X> type of value
   * @return NOT_NULL or result of constraint
   */
  private static <X> Function<X, Optional<DomainError>> nonNull(
      Function<X, Optional<DomainError>> constraint) {
    return value -> notNull().apply(value).or(() -> constraint.apply(value));
  }
}
