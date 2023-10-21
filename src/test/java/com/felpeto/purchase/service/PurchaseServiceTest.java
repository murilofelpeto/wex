package com.felpeto.purchase.service;


import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import com.felpeto.purchase.client.FiscalData;
import com.felpeto.purchase.model.exceptions.InvalidDateException;
import com.felpeto.purchase.model.exceptions.ValueNotFoundException;
import com.felpeto.purchase.repository.PurchaseEntity;
import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

  private final Faker faker = new Faker();
  @Mock
  private FiscalData fiscalData;

  @InjectMocks
  private PurchaseService service;

  @Test
  void givenUuidAndCountryWhenThereIsNoEntityThenThrowException() {

    try (MockedStatic<PurchaseEntity> mockEntity = Mockito.mockStatic(PurchaseEntity.class)) {
      final var uuid = UUID.randomUUID();
      final var country = faker.country().name();

      mockEntity.when(() -> PurchaseEntity.findByUuid(uuid)).thenReturn(Optional.empty());

      final var exception = catchThrowableOfType(() -> service.getPurchaseTransaction(uuid, country),
          ValueNotFoundException.class);

      assertThat(exception.getMessage()).isEqualTo("Purchase not found");
      assertThat(exception.getParameter()).isEqualTo("uuid");
      assertThat(exception.getTarget()).isEqualTo("Purchase");
      assertThat(exception.getField()).isEqualTo("uuid");
      assertThat(exception.getViolationMessage()).isEqualTo("Purchase not found");
    }
  }

  @Test
  void givenUuidAndCountryWhenEntityHasInvalidDataThenThrowException() {

    try (MockedStatic<PurchaseEntity> mockEntity = Mockito.mockStatic(PurchaseEntity.class)) {
      final var uuid = UUID.randomUUID();
      final var country = faker.country().name();
      final var entity = createPurchaseEntity(LocalDate.now().minusMonths(7));

      mockEntity.when(() -> PurchaseEntity.findByUuid(uuid)).thenReturn(Optional.of(entity));

      final var exception = catchThrowableOfType(() -> service.getPurchaseTransaction(uuid, country),
          InvalidDateException.class);

      assertThat(exception.getMessage()).isEqualTo("Transaction date is older than 6 months.");
      assertThat(exception.getParameter()).isEqualTo("transactionDate");
      assertThat(exception.getTarget()).isEqualTo("Purchase");
      assertThat(exception.getField()).isEqualTo("transactionDate");
      assertThat(exception.getViolationMessage()).isEqualTo("Transaction date is older than 6 months.");
    }
  }

  private PurchaseEntity createPurchaseEntity(final LocalDate transactionDate) {
    final var entity = new PurchaseEntity();
    entity.id = 1L;
    entity.uuid = UUID.randomUUID();
    entity.description = faker.hitchhikersGuideToTheGalaxy().quote();
    entity.transactionDate = transactionDate;
    entity.amount = BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)).setScale(2, HALF_UP);

    return entity;
  }

}