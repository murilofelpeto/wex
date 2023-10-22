package com.felpeto.purchase.service;

import static com.felpeto.purchase.service.mapper.PurchaseMapper.toPurchase;
import static java.time.format.DateTimeFormatter.ISO_DATE;

import com.felpeto.purchase.client.FiscalData;
import com.felpeto.purchase.client.dto.Data;
import com.felpeto.purchase.model.Purchase;
import com.felpeto.purchase.model.exceptions.InvalidDateException;
import com.felpeto.purchase.model.exceptions.ValueNotFoundException;
import com.felpeto.purchase.repository.PurchaseEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Named
@ApplicationScoped
public class PurchaseService {

  private static final String FISCAL_DATA_FIELDS = "record_date, currency, exchange_rate";
  private static final LocalDate TODAY = LocalDate.now();
  private static final String SORT_RECORD_DATE = "-record_date";
  private final FiscalData fiscalData;

  public PurchaseService(@RestClient final FiscalData fiscalData) {
    this.fiscalData = fiscalData;
  }

  private static long compareDate(final Data date) {
    return Math.abs(
        ChronoUnit.DAYS.between(
            TODAY,
            LocalDate.parse(date.getRecordDate(), ISO_DATE)));
  }

  @Transactional
  public Purchase save(final Purchase purchase) {
    final var entity = new PurchaseEntity();
    entity.id = null;
    entity.uuid = UUID.randomUUID();
    entity.description = purchase.getDescription().getValue();
    entity.transactionDate = purchase.getTransactionDate();
    entity.amount = purchase.getAmount().getValue();

    entity.persist();
    return toPurchase(entity);
  }

  public Purchase getPurchaseTransaction(final UUID uuid, final String country) {
    final var optEntity = PurchaseEntity.findByUuid(uuid);

    if (optEntity.isEmpty()) {
      throw new ValueNotFoundException("Purchase not found",
          "uuid",
          "Purchase",
          "uuid",
          "Purchase not found");
    }

    final var entity = optEntity.get();
    final var transactionDate = entity.transactionDate;

    if (isTransactionDateInvalid(transactionDate)) {
      throw new InvalidDateException("Transaction date is older than 6 months.",
          "transactionDate",
          "Purchase",
          "transactionDate",
          "Transaction date is older than 6 months.");
    }

    final var filter = "country:eq:"
        + country
        + ",record_date:lte:"
        + transactionDate;

    final var response = fiscalData.getCurrencyRate(FISCAL_DATA_FIELDS, filter, SORT_RECORD_DATE);
    final var data = findClosestDate(response.getData());

    return toPurchase(entity, data);
  }

  private Data findClosestDate(final List<Data> dataList) {
    return dataList.stream()
        .min(Comparator.comparing(PurchaseService::compareDate))
        .orElseThrow(() -> new InvalidDateException("Could not find closest date",
            "transactionDate",
            "Purchase",
            "transactionDate",
            "There is no data for your request."));
  }

  private boolean isTransactionDateInvalid(final LocalDate transactionDate) {
    return transactionDate.isBefore(TODAY.minusMonths(6));
  }
}
