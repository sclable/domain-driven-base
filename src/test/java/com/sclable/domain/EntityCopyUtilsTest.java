package com.sclable.domain;

import java.util.UUID;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EntityCopyUtilsTest {
  static class TestingEntityId extends EntityId {
    public TestingEntityId() {
      super(UUID.randomUUID());
    }
  }

  static class TestingEntity extends Entity<TestingEntityId> {
    public TestingEntity(TestingEntityId id) {
      super(id);
    }
  }

  static class OrderedTestingEntities extends OrderedEntities<TestingEntityId, TestingEntity> {}

  static class TestingAggregateId extends AggregateId {
    public TestingAggregateId() {
      super(UUID.randomUUID());
    }
  }

  static class TestingAggregate extends AggregateRoot<TestingAggregateId> {
    final OrderedTestingEntities orderedTestingEntities;
    @Getter final TestingEntity nestedEntity;

    public TestingAggregate() {
      super(new TestingAggregateId());
      this.orderedTestingEntities = new OrderedTestingEntities();
      this.orderedTestingEntities.add(new TestingEntity(new TestingEntityId()));
      this.nestedEntity = new TestingEntity(new TestingEntityId());
    }

    public TestingEntity getOrderedEntityItem() {
      return orderedTestingEntities.last().get();
    }
  }

  @Test
  void whenEntityIsCopiedIdOfCopyDiffers() {
    var originalAggregate = new TestingAggregate();
    var copiedAggregate = EntityCopyUtils.copy(originalAggregate);
    Assertions.assertNotEquals(originalAggregate.getId(), copiedAggregate.getId());
    Assertions.assertTrue(copiedAggregate.isCopied());
    Assertions.assertEquals(originalAggregate.getId(), copiedAggregate.getOriginalId());
  }

  @Test
  void whenEntityWithNestedOrderedEntitiesIsCopiedIdOfCopyDiffers() {
    var originalAggregate = new TestingAggregate();
    var copiedAggregate = EntityCopyUtils.copy(originalAggregate);
    Assertions.assertNotEquals(
        originalAggregate.getOrderedEntityItem().getId(),
        copiedAggregate.getOrderedEntityItem().getId());
    Assertions.assertTrue(copiedAggregate.isCopied());
    Assertions.assertEquals(originalAggregate.getId(), copiedAggregate.getOriginalId());
  }

  @Test
  void whenEntityWithNestedEntityIsCopiedIdOfCopyDiffers() {
    var originalAggregate = new TestingAggregate();
    var copiedAggregate = EntityCopyUtils.copy(originalAggregate);
    Assertions.assertNotEquals(
        originalAggregate.getNestedEntity().getId(), copiedAggregate.getNestedEntity().getId());
    Assertions.assertTrue(copiedAggregate.isCopied());
    Assertions.assertEquals(originalAggregate.getId(), copiedAggregate.getOriginalId());
  }
}
