package com.sclable.domain.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DomainException extends RuntimeException {
  @Getter private final DomainError error;
  @Getter private final List<DomainError> errorDetails = new ArrayList<>();

  public DomainException(DomainError error) {
    super(error.getMessage());
    this.error = error;
  }

  public DomainException(DomainError error, Throwable cause) {
    super(error.getMessage(), cause);
    this.error = error;
  }

  public DomainException(DomainError error, DomainException cause) {
    super(error.getMessage(), cause);
    this.error = error;
    this.errorDetails.addAll(cause.getErrorDetails());
  }

  public DomainException(ERR errorEnum) {
    super(errorEnum.getMessage());
    this.error = errorEnum.toError();
  }

  public DomainException(DomainError error, List<DomainError> errorDetails) {
    super(error.getMessage());
    this.error = error;
    this.errorDetails.addAll(errorDetails);
  }

  public String getMessage() {
    var message = new StringBuilder();
    message.append(error.getMessage());

    if (!errorDetails.isEmpty()) {
      message.append(":");
      errorDetails.forEach(
          domainError -> message.append("\n\t- ").append(domainError.getMessage()));
    }

    return message.toString();
  }

  public ERR getErrorCode() {
    return this.error.getError();
  }

  public enum ERR {
    DOMAIN_CONSTRAINTS_VIOLATED("Domain constraints violated in {0}"),
    DOMAIN_SERVICE_CONSTRAINTS_VIOLATED("Domain service constraints violated"),

    // CONSTRAINT VIOLATIONS
    NOT_NULL("Property {0}#{1} must not be null"),
    IS_NULL("Property {0}#{1} must be null"),
    NOT_POSITIVE("Property {0}#{1} must be positive but is {2} instead"),
    NOT_ZERO_OR_POSITIVE("Property {0}#{1} must be zero or positive but is {2} instead"),
    NOT_NEGATIVE("Property {0}#{1} must be negative but is {2} instead"),
    NOT_ZERO_OR_NEGATIVE("Property {0}#{1} must be zero or negative but is {2} instead"),
    NOT_LESS_THAN_OR_EQUAL("Property {1}#{2} with value {3} must be less than or equal to {0}"),
    NOT_BLANK("Property {0}#{1} must not be blank"),
    IS_EMPTY("Property {0}#{1} must must not be empty"),
    STRING_OUT_OF_BOUNDS(
        "Property {0}#{1} must be within {1} and {2} characters but has {4} characters"),
    NUMBER_OUT_OF_BOUNDS("Property {0}#{1} must be within {2} and {3} but is {4} instead"),
    PERCENTAGE_OUT_OF_BOUNDS("Property {0}#{1} must be within {2} and {3} but is {4} instead"),
    IS_UNIQUE("Property {0}#{1} must be unique"),
    LITERAL_VIOLATION("Property {0}#{1} must be same as #{2}"),
    NOT_IN_PAST("Property {0}#{1} must not be in past"),
    NOT_IN_FUTURE("Property {0}#{1} must not be in future"),
    TODAY_OR_IN_FUTURE("Property {0}#{1} must be either today or date in the future."),
    ORDER_NEEDS_TO_SPECIFY_ALL_IDS("Argument {0}#{1} must specify all available ids."),
    NOT_UNIQUE("List {0}#{1} does not contain unique values only."),
    COLLECTION_CONTAINS("Collection already contains an element with {1}={2}."),
    INVALID_FORMAT("Value {0} is in invalid format."),
    CAN_NOT_ASSUME_ATTRIBUTE(
        "Can not assume attribute that was set as an outcome of a transformation."),
    SAME_ALREADY_ASSIGNED("{1} with id={2} is already assigned to {0}"),
    ANOTHER_ALREADY_ASSIGNED(
        "{Another {1} with id={3} is already assigned to {0}. Cannot assign {1} with id={2}"),
    NOT_BEFORE("Value {0} must not be before {1}"),
    NOT_AFTER("Value {0} must not be after {1}"),
    VALUE_NOT_IN_COLLECTION("Value={2} is not an element in the collection."),
    CAN_NOT_ADD_EXISTING_ENTITY("Cannot add already existing entity {0}"),
    CAN_NOT_REMOVE_UNKNOWN_ENTITY("Cannot remove unknown entity {0}"),

    // AGGREGATE
    ENTITY_NOT_FOUND("Entity {0} not found by id={1}"),
    ENTITY_ID_NOT_FOUND("Entity ID {0}={1} not found"),
    RELATED_ENTITY_NOT_FOUND("Related entity {0} not found by id={1}"),
    RELATED_ENTITY_NOT_PRESENT("Related entity class {0} is not available"),
    AGGREGATE_NOT_FOUND("Aggregate {0} not found by id={1}"),
    AGGREGATE_NOT_FOUND_BY("Aggregate {0} not found by {1}={2}"),
    ENTITY_ALREADY_EXISTS("Entity {0} with id={1} already exists"),
    AGGREGATE_ALREADY_EXISTS("Aggregate {0} with id={1} already exists"),
    ENTITY_ID_ALREADY_EXISTS("Entity ID {0}={1} already exists"),
    MODIFIED_ENTITY_NOT_FOUND("Modified entity {0} not found by id={1}"),

    RELATION_NOT_SET("Relation from {0} to {1} could not be set"),
    INSTANCE_NOT_CREATED("Instance of class {0} cannot be created"),
    CLASS_NOT_FOUND("Class {0} not found"),
    CANNOT_ACCESS_FIELD("Field {0} cannot be accessed"),
    AMBIGUOUS_ENTITIES_FOUND("Multiple entities found for a relation with cardinality 1"),
    RETRIEVE_CACHE_KEY_FAILED("Cannot retrieve cache key."),
    RETRIEVE_CACHED_AGGREGATE_FAILED("Cannot retrieve cached aggregate."),
    CACHE_KEY_MISSING("Cannot retrieve cached objects without a cache key.");

    private final String message;

    ERR(String message) {
      this.message = message;
    }

    public DomainError toError() {
      return new DomainError(this);
    }

    public final DomainError parameters(Parameter... parameters) {
      var domainError = new DomainError(this);
      domainError.addParameters(parameters);
      return domainError;
    }

    public final String getMessage() {
      return this.message;
    }
  }

  @RequiredArgsConstructor(staticName = "of")
  public static final class Parameter implements Serializable {
    @Getter @NonNull private final String key;
    @Getter private final Object value;

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return this.value != null ? this.value.toString() : "[null]";
    }
  }

  public static class DomainError implements Serializable {
    @Getter private final ERR error;
    @Getter private final List<Parameter> parameters;

    public DomainError(ERR error) {
      this.error = error;
      this.parameters = new ArrayList<>();
    }

    public final String getMessage() {
      return MessageFormat.format(this.error.getMessage(), this.getParameters().toArray());
    }

    public final DomainError addParameters(Parameter... parameters) {
      this.parameters.addAll(Arrays.asList(parameters));
      return this;
    }
  }
}
