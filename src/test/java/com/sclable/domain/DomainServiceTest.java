package com.sclable.domain;

import static com.sclable.domain.ConstraintValidations.constraint;
import static com.sclable.domain.ConstraintValidations.notNull;
import static org.junit.jupiter.api.Assertions.*;

import com.sclable.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DomainServiceTest {
  @Test
  @DisplayName("ensure throws exception when invalid parameter is passed")
  void testEnsureWithInvalidParameter() {
    var myService = new MyDomainService();

    assertThrows(DomainException.class, () -> myService.myEnsure(null));
  }

  @Test
  @DisplayName("ensure doesn't throw exception when valid parameter is passed")
  void testEnsureWithValidParameter() {
    var myService = new MyDomainService();

    assertDoesNotThrow(() -> myService.myEnsure(new Object()));
  }

  private static class MyEntityId extends EntityId {}

  private static class MyEntity extends Entity<MyEntityId> {
    public MyEntity(MyEntityId id) {
      super(id);
    }
  }

  private static class MyDomainService extends DomainService {
    public void myEnsure(Object obj) {
      ensure(constraint("obj", obj, notNull()));
    }
  }
}
