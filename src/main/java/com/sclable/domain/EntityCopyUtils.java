package com.sclable.domain;

import com.sclable.domain.exception.DomainException;
import org.apache.commons.lang3.SerializationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EntityCopyUtils is used to copy entities and aggregates including nested Entity and Entities. In
 * comparison to SerializationUtils.clone(entity) new ids are assigned to all domain objects.
 *
 * @example var myEntity = new MyEntity(); var copiedEntity = EntityCopyUtils.copy(myEntity);
 *     myEntity.getId().equals(copiedEntity.getId()); // false
 */
public class EntityCopyUtils<ID extends EntityId, E extends Entity<ID>> {
  private final ID entityId;
  private final E entity;
  private final HashMap<Field, Entities<? extends EntityId, ? extends Entity<?>>> entitiesMap = new HashMap<>();
  private final HashMap<Field, Entity<? extends EntityId>> entityMap = new HashMap<>();

  public static <Id extends EntityId, E extends Entity<Id>> E copy(E entity) {
    return new EntityCopyUtils<>(entity).copy();
  }

  public static <Id extends EntityId, E extends Entity<Id>> Set<E> copy(Set<E> entities) {
    return entities.stream().map(EntityCopyUtils::copy).collect(Collectors.toSet());
  }

  private EntityCopyUtils(E entity) {
    this.entity = entity;
    this.entityId = entity.getId();
  }

  private E copy() {
    storeOriginalFields();
    var copy = reassignIdAndCopy();
    copy.markAsCopied();
    copy.assignOriginalId(this.entityId);
    restoreOriginals();

    return copy;
  }

  public void storeOriginalFields() {
    getEntitiesFields().forEach(field -> entitiesMap.put(field, (Entities<? extends EntityId, ? extends Entity<?>>) getFieldValue(field)));

    getEntityFields().forEach(field -> entityMap.put(field, (Entity<? extends EntityId>) getFieldValue(field)));
  }

  public E reassignIdAndCopy() {
    setId((ID) newInstanceOf(this.entityId.getClass()));
    entitiesMap.forEach(
        (field, entities) -> {
          if (Objects.nonNull(entities)) {
            setField(field, entities.copy());
          }
        });
    entityMap.forEach(
        (field, tmpEntity) -> {
          if (Objects.nonNull(tmpEntity)) {
            setField(field, tmpEntity.copy());
          }
        });

    return SerializationUtils.clone(entity);
  }

  public void restoreOriginals() {
    setId(this.entityId);
    entitiesMap.forEach(this::setField);
    entityMap.forEach(this::setField);
  }

  private void setId(ID id) {
    var field = findFieldByName("id");

    if (field != null) {
      setField(field, id);
    }
  }

  private List<Field> getEntitiesFields() {
    return getFieldsOfType(Entities.class);
  }

  private List<Field> getEntityFields() {
    return getFieldsOfType(Entity.class);
  }

  private List<Field> getFieldsOfType(Class<?> entitiesClass) {
    return getFields(entity).stream()
        .filter((field) -> entitiesClass.isAssignableFrom(field.getType()))
        .collect(Collectors.toList());
  }

  private <T> List<Field> getFields(T t) {
    var fields = new ArrayList<Field>();
    var clazz = t.getClass();
    while (clazz != Object.class) {
      fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
      clazz = clazz.getSuperclass();
    }
    return fields;
  }

  private <T> void setField(Field field, T value) {
    try {
      field.setAccessible(true);
      field.set(entity, value);
    } catch (IllegalAccessException e) {
      throw new DomainException(
              DomainException.ERR.CANNOT_ACCESS_FIELD.parameters(
                      DomainException.Parameter.of("field", field.getName())));
    }
  }

  private <T> T newInstanceOf(Class<? extends T> aClass) {
    try {
      return aClass.getConstructor().newInstance();
    } catch (NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException
        | InstantiationException e) {
      throw new DomainException(
              DomainException.ERR.INSTANCE_NOT_CREATED.parameters(
                      DomainException.Parameter.of("class", aClass.getSimpleName())));
    }
  }

  private Object getFieldValue(Field field) {
    try {
      field.setAccessible(true);

      return field.get(entity);
    } catch (IllegalAccessException e) {
      throw new DomainException(
              DomainException.ERR.CANNOT_ACCESS_FIELD.parameters(
                      DomainException.Parameter.of("field", field.getName())));
    }
  }

  private Field findFieldByName(String name) {
    return getFields(entity).stream()
        .filter(field -> name.equals(field.getName()))
        .findFirst()
        .orElse(null);
  }
}
