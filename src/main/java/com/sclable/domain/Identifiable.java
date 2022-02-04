package com.sclable.domain;

import java.io.Serializable;

/**
 * Indicates that some type has an identifier, usually unique for all objects of the same type. The
 * only requirement to conform with this interface is to provide {@link #getId()} which returns the
 * identifier of an object.
 *
 * @param <T> is the type of the identifier being used, for example {@link java.util.UUID} or {@link
 *     String}.
 */
@FunctionalInterface
public interface Identifiable<T> extends Serializable {
  T getId();
}
