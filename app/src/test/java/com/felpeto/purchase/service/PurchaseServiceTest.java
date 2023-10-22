package com.felpeto.purchase.service;


import static java.math.RoundingMode.HALF_UP;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.felpeto.purchase.client.FiscalData;
import com.felpeto.purchase.client.dto.CurrencyExchangeResponseDto;
import com.felpeto.purchase.client.dto.Data;
import com.felpeto.purchase.model.Purchase;
import com.felpeto.purchase.model.exceptions.InvalidDateException;
import com.felpeto.purchase.model.exceptions.ValueNotFoundException;
import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import com.felpeto.purchase.repository.PurchaseEntity;
import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

  private static final String FISCAL_DATA_FIELDS = "record_date, currency, exchange_rate";
  private static final String SORT_RECORD_DATE = "-record_date";
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

      final var exception = catchThrowableOfType(
          () -> service.getPurchaseTransaction(uuid, country),
          ValueNotFoundException.class);

      assertThat(exception.getMessage()).isEqualTo("Purchase not found");
      assertThat(exception.getParameter()).isEqualTo("uuid");
      assertThat(exception.getTarget()).isEqualTo("Purchase");
      assertThat(exception.getField()).isEqualTo("uuid");
      assertThat(exception.getViolationMessage()).isEqualTo("Purchase not found");
    }
    verifyNoInteractions(fiscalData);
  }

  @Test
  void givenUuidAndCountryWhenEntityHasInvalidDataThenThrowException() {
    try (MockedStatic<PurchaseEntity> mockEntity = Mockito.mockStatic(PurchaseEntity.class)) {
      final var uuid = UUID.randomUUID();
      final var country = faker.country().name();
      final var entity = createPurchaseEntity(LocalDate.now().minusMonths(7));

      mockEntity.when(() -> PurchaseEntity.findByUuid(uuid)).thenReturn(Optional.of(entity));

      final var exception = catchThrowableOfType(
          () -> service.getPurchaseTransaction(uuid, country),
          InvalidDateException.class);

      assertThat(exception.getMessage()).isEqualTo("Transaction date is older than 6 months.");
      assertThat(exception.getParameter()).isEqualTo("transactionDate");
      assertThat(exception.getTarget()).isEqualTo("Purchase");
      assertThat(exception.getField()).isEqualTo("transactionDate");
      assertThat(exception.getViolationMessage()).isEqualTo(
          "Transaction date is older than 6 months.");
    }
    verifyNoInteractions(fiscalData);
  }

  @Test
  void givenUuidAndCountryWhenEntityHasValidDataThenReturnPurchase() {
    try (MockedStatic<PurchaseEntity> mockEntity = Mockito.mockStatic(PurchaseEntity.class)) {
      final var uuid = UUID.randomUUID();
      final var country = faker.country().name();
      final var entity = createPurchaseEntity(LocalDate.now().minusMonths(6));
      final var clientResponse = createClientResponse(entity.transactionDate);
      final var filter = "country:eq:"
          + country
          + ",record_date:lte:"
          + entity.transactionDate;

      when(fiscalData.getCurrencyRate(FISCAL_DATA_FIELDS, filter, SORT_RECORD_DATE)).thenReturn(
          clientResponse);

      mockEntity.when(() -> PurchaseEntity.findByUuid(uuid)).thenReturn(Optional.of(entity));

      final var response = service.getPurchaseTransaction(uuid, country);

      assertThat(response).isNotNull();
      verify(fiscalData).getCurrencyRate(FISCAL_DATA_FIELDS, filter, SORT_RECORD_DATE);
    }
  }

  @Test
  void givenEmptyDataWhenClientResponseIsInvalidThenThrowException() {
    try (MockedStatic<PurchaseEntity> mockEntity = Mockito.mockStatic(PurchaseEntity.class)) {
      final var uuid = UUID.randomUUID();
      final var country = faker.country().name();
      final var entity = createPurchaseEntity(LocalDate.now().minusMonths(2));
      final var clientResponse = CurrencyExchangeResponseDto.builder()
          .data(emptyList())
          .build();
      final var filter = "country:eq:"
          + country
          + ",record_date:lte:"
          + entity.transactionDate;

      mockEntity.when(() -> PurchaseEntity.findByUuid(uuid)).thenReturn(Optional.of(entity));
      when(fiscalData.getCurrencyRate(FISCAL_DATA_FIELDS, filter, SORT_RECORD_DATE)).thenReturn(
          clientResponse);

      final var exception = catchThrowableOfType(
          () -> service.getPurchaseTransaction(uuid, country),
          InvalidDateException.class);

      assertThat(exception.getMessage()).isEqualTo("Could not find closest date");
      assertThat(exception.getParameter()).isEqualTo("transactionDate");
      assertThat(exception.getTarget()).isEqualTo("Purchase");
      assertThat(exception.getField()).isEqualTo("transactionDate");
      assertThat(exception.getViolationMessage()).isEqualTo(
          "There is no data for your request.");
    }
  }
  @Test
  void givenFutureDataWhenClientResponseIsInvalidThenThrowException() {
    try (MockedStatic<PurchaseEntity> mockEntity = Mockito.mockStatic(PurchaseEntity.class)) {
      final var uuid = UUID.randomUUID();
      final var country = faker.country().name();
      final var entity = createPurchaseEntity(LocalDate.now());
      final var farawayDate = Data.builder()
          .currency(faker.currency().name())
          .exchangeRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
          .recordDate(LocalDate.now().minusMonths(8).toString())
          .build();

      final var futureCurrency = faker.currency().name();
      final var futureExchangeRate = BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100));
      final var futureDate = Data.builder()
          .currency(futureCurrency)
          .exchangeRate(futureExchangeRate)
          .recordDate(LocalDate.now().plusMonths(1).toString())
          .build();
      final var clientResponse = CurrencyExchangeResponseDto.builder()
          .data(List.of(farawayDate, futureDate))
          .build();
      final var filter = "country:eq:"
          + country
          + ",record_date:lte:"
          + entity.transactionDate;

      mockEntity.when(() -> PurchaseEntity.findByUuid(uuid)).thenReturn(Optional.of(entity));
      when(fiscalData.getCurrencyRate(FISCAL_DATA_FIELDS, filter, SORT_RECORD_DATE)).thenReturn(
          clientResponse);

      final var response = service.getPurchaseTransaction(uuid, country);

      final var convertedMoney = Money.roundUp(entity.amount.multiply(futureExchangeRate));
      assertThat(response.getExchangeRate().getValue()).isEqualTo(futureExchangeRate);
      assertThat(response.getConvertedMoney()).isEqualTo(convertedMoney);
    }
  }

  @Test
  void givenSameDateWhenClientResponseIsInvalidThenThrowException() {
    try (MockedStatic<PurchaseEntity> mockEntity = Mockito.mockStatic(PurchaseEntity.class)) {
      final var uuid = UUID.randomUUID();
      final var country = faker.country().name();
      final var entity = createPurchaseEntity(LocalDate.now());
      final var farawayDate = Data.builder()
          .currency(faker.currency().name())
          .exchangeRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
          .recordDate(LocalDate.now().minusMonths(8).toString())
          .build();

      final var futureCurrency = faker.currency().name();
      final var futureExchangeRate = BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100));
      final var futureDate = Data.builder()
          .currency(futureCurrency)
          .exchangeRate(futureExchangeRate)
          .recordDate(LocalDate.now().toString())
          .build();
      final var clientResponse = CurrencyExchangeResponseDto.builder()
          .data(List.of(farawayDate, futureDate))
          .build();
      final var filter = "country:eq:"
          + country
          + ",record_date:lte:"
          + entity.transactionDate;

      mockEntity.when(() -> PurchaseEntity.findByUuid(uuid)).thenReturn(Optional.of(entity));
      when(fiscalData.getCurrencyRate(FISCAL_DATA_FIELDS, filter, SORT_RECORD_DATE)).thenReturn(
          clientResponse);

      final var response = service.getPurchaseTransaction(uuid, country);

      final var convertedMoney = Money.roundUp(entity.amount.multiply(futureExchangeRate));
      assertThat(response.getExchangeRate().getValue()).isEqualTo(futureExchangeRate);
      assertThat(response.getConvertedMoney()).isEqualTo(convertedMoney);
    }
  }

  private CurrencyExchangeResponseDto createClientResponse(final LocalDate transactionDate) {
    final var dateTo = transactionDate.plusMonths(faker.number().randomDigitNotZero());
    final var dateFrom = transactionDate.minusMonths(faker.number().randomDigitNotZero());

    final var data1 = Data.builder()
        .currency(faker.currency().name())
        .exchangeRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
        .recordDate(dateTo.toString())
        .build();
    final var data2 = Data.builder()
        .currency(faker.currency().name())
        .exchangeRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
        .recordDate(dateFrom.toString())
        .build();

    return CurrencyExchangeResponseDto.builder()
        .data(List.of(data1, data2))
        .build();

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

  private Purchase createPurchase() {
    final var description = Description.of(faker.harryPotter().quote());
    final var transactionDate = LocalDate.now();
    final var amount = Money.roundUp(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));

    return Purchase.builder()
        .amount(amount)
        .description(description)
        .transactionDate(transactionDate)
        .build();
  }

}