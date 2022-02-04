package com.sclable.domain;

import com.sclable.domain.exception.DomainException;

import java.util.*;
import java.util.stream.Collectors;

import static com.sclable.domain.ConstraintValidations.constraint;
import static com.sclable.domain.ConstraintValidations.notEmpty;

public abstract class OrderedEntities<ID extends EntityId, ENTITY extends Entity<ID>>
    extends Entities<ID, ENTITY> {
  public void replaceAll(List<ENTITY> entities) {
    clear();
    entities.forEach(this::add);
  }

  @Override
  protected void initialize() {
    entities = new LinkedHashSet<>();
  }

  public List<ENTITY> getAllSorted() {
    return new ArrayList<>(entities);
  }

  public void orderByEntityIds(List<ID> entityIds) {
    ensure(constraint("entityIds", entityIds, notEmpty()));

    var containsUnspecifiedEntities =
        entities.stream().anyMatch(entity -> !entityIds.contains(entity.getId()));

    if (containsUnspecifiedEntities) {
      throw new DomainException(
          DomainException.ERR.ORDER_NEEDS_TO_SPECIFY_ALL_IDS.parameters(
              DomainException.Parameter.of("argumentName", "priorities"),
              DomainException.Parameter.of("value", entityIds)));
    }

    var reorderedEntities =
        entityIds.stream()
            .map(
                priority ->
                    entities.stream()
                        .filter((entity) -> entity.getId().equals(priority))
                        .findFirst())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

    entities.clear();
    entities.addAll(reorderedEntities);
  }

  public void sort(Comparator<ENTITY> comparator) {
    var sortedEntities = entities.stream().sorted(comparator).collect(Collectors.toList());

    clear();
    entities.addAll(sortedEntities);
  }

  public Optional<ENTITY> last() {
    ENTITY last = null;

    for (var e : entities) {
      last = e;
    }

    return Optional.ofNullable(last);
  }
}
