package com.sclable.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.sclable.domain.exception.DomainException;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EntityIdsTest {
  @Test
  void testIsEmpty() {
    var testIds = new TestIds();
    assertTrue(testIds.isEmpty());
  }

  @Test
  void testContainsUUID() {
    UUID uuid1 = UUID.randomUUID();
    var testId = new TestId(uuid1);

    var testIds = new TestIds();
    testIds.add(testId);

    assertTrue(testIds.contains(testId));
    assertTrue(testIds.contains(new TestId(uuid1)));
  }

  @Test
  void testAddAll() {
    var uuid1 = UUID.randomUUID();
    var uuid2 = UUID.randomUUID();
    var uuid3 = UUID.randomUUID();

    var testIds = new TestIds();
    testIds.addAll(Set.of(new TestId(uuid1), new TestId(uuid2), new TestId(uuid3)));
    assertTrue(
        testIds
            .getAll()
            .containsAll(Set.of(new TestId(uuid1), new TestId(uuid2), new TestId(uuid3))));
  }

  @Test
  void testClear() {
    var testIds = new TestIds();
    testIds.addAll(getTestIds());
    assertFalse(testIds.isEmpty());
    testIds.clear();
    assertTrue(testIds.isEmpty());
  }

  @Test
  void testSize() {
    var testIds = new TestIds();
    testIds.addAll(getTestIds());
    assertEquals(3, testIds.size());
    testIds.addAll(getTestIds());
    assertEquals(6, testIds.size());
  }

  @Test
  void testAddOfExistingThrowsException() {
    var testIds = new TestIds();
    var testId1 = new TestId();
    testIds.add(testId1);
    assertThrows(DomainException.class, () -> testIds.add(testId1));
  }

  @Test
  void testRemoveOfNonExistingThrowsException() {
    var testIds = new TestIds();
    var testId1 = new TestId();
    assertThrows(DomainException.class, () -> testIds.remove(testId1));
  }

  @Test
  void testRemove() {
    var testIds = new TestIds();
    var testId1 = new TestId();
    testIds.add(testId1);
    testIds.remove(testId1);
    assertTrue(testIds.isEmpty());
  }

  private Set<TestId> getTestIds() {
    var testId1 = new TestId();
    var testId2 = new TestId();
    var testId3 = new TestId();
    return Set.of(testId1, testId2, testId3);
  }

  private class TestId extends EntityId {
    public TestId() {}

    public TestId(UUID id) {
      super(id);
    }
  }

  private class TestIds extends EntityIds<TestId> {}
}
