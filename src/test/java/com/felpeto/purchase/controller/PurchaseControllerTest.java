package com.felpeto.purchase.controller;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.felpeto.purchase.controller.dto.request.PurchaseRequestDto;
import com.felpeto.purchase.model.Purchase;
import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import com.felpeto.purchase.service.PurchaseService;
import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

  private final Faker faker = new Faker();

  @Mock
  private PurchaseService service;

  @InjectMocks
  private PurchaseController controller;

  @Test
  void givenPurchaseRequestDtoWhenSaveThenReturnPurchaseResponseDto() {
    final var now = LocalDate.now();
    final var uuid = UUID.randomUUID();
    final var request = PurchaseRequestDto.builder()
        .amount(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
        .description(faker.overwatch().location())
        .transactionDate(now)
        .build();
    final var purchase = createPurchase(request, null);
    final var persistedPurchase = createPurchase(request, uuid);

    when(service.save(purchase)).thenReturn(persistedPurchase);

    final var response = controller.save(request);

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(CREATED.getStatusCode());

    verify(service).save(purchase);
    verify(service, never()).getPurchaseTransaction(any(UUID.class), anyString());
    verifyNoMoreInteractions(service);
  }

  @Test
  void givenUuidAndCountryWhenGetPurchaseThenReturnPurchaseResponseDto() {
    final var uuid = UUID.randomUUID();
    final var country = faker.country().name();
    final var purchase = createPurchaseWithExchangeRate(uuid);

    when(service.getPurchaseTransaction(uuid, country)).thenReturn(purchase);

    final var response = controller.get(uuid, country);

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());

    verify(service).getPurchaseTransaction(uuid, country);
    verify(service, never()).save(any());
    verifyNoMoreInteractions(service);
  }

  private Purchase createPurchaseWithExchangeRate(final UUID uuid) {
    final var amount = BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100));
    final var exchangeRate = BigDecimal.valueOf(faker.number().randomDouble(4, 1, 100));
    return Purchase.withExchangeRate()
        .amount(Money.roundUp(amount))
        .description(Description.of(faker.harryPotter().quote()))
        .exchangeRate(Money.of(exchangeRate))
        .transactionDate(LocalDate.now())
        .uuid(uuid)
        .buildWithExchangeRate();
  }

  private Purchase createPurchase(final PurchaseRequestDto request, final UUID uuid) {
    return Purchase.builder()
        .amount(Money.roundUp(request.getAmount()))
        .description(Description.of(request.getDescription()))
        .transactionDate(request.getTransactionDate())
        .uuid(uuid)
        .build();
  }
}