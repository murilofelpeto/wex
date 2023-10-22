package com.felpeto.purchase.repository;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseEntityTest {

  private final Faker faker = new Faker();

  @Test
  void givenUuidWhenFindByUuidThenReturnOptionalOfPurchaseEntity() {
    final var panacheQuery = mock(PanacheQuery.class);
    try (MockedStatic<PanacheEntityBase> mockEntity = Mockito.mockStatic(PanacheEntityBase.class)) {
      final var uuid = UUID.randomUUID();
      final var entity = new PurchaseEntity();
      entity.id = 1L;
      entity.uuid = UUID.randomUUID();
      entity.description = faker.hitchhikersGuideToTheGalaxy().quote();
      entity.transactionDate = LocalDate.now();
      entity.amount = BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100))
          .setScale(2, HALF_UP);

      mockEntity.when(() -> PanacheEntityBase.find("uuid", uuid)).thenReturn(panacheQuery);
      when(panacheQuery.firstResultOptional()).thenReturn(Optional.of(entity));

      final var response = PurchaseEntity.findByUuid(uuid);

      assertThat(response).isNotEmpty();
      assertThat(response.get()).isEqualTo(entity);
    }
  }

  @Test
  void givenUuidWhenFindByUuidThenReturnEmptyOptional() {
    final var panacheQuery = mock(PanacheQuery.class);
    try (MockedStatic<PanacheEntityBase> mockEntity = Mockito.mockStatic(PanacheEntityBase.class)) {
      final var uuid = UUID.randomUUID();

      mockEntity.when(() -> PanacheEntityBase.find("uuid", uuid)).thenReturn(panacheQuery);
      when(panacheQuery.firstResultOptional()).thenReturn(Optional.empty());

      final var response = PurchaseEntity.findByUuid(uuid);

      assertThat(response).isEmpty();
    }
  }
}