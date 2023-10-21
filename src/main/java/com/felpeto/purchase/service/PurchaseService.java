package com.felpeto.purchase.service;

import static com.felpeto.purchase.service.mapper.PurchaseMapper.toPurchase;

import com.felpeto.purchase.model.Purchase;
import com.felpeto.purchase.repository.PurchaseEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.PrePersist;
import jakarta.transaction.Transactional;
import java.util.UUID;

@Named
@ApplicationScoped
public class PurchaseService {

  private final PurchaseEntity entity;

  public PurchaseService(final PurchaseEntity entity) {
    this.entity = new PurchaseEntity(entity);
  }

  @Transactional
  public Purchase save(final Purchase purchase) {
    final var entity = new PurchaseEntity();
    entity.id = null;
    entity.description = purchase.description().getValue();
    entity.transactionDate = purchase.transactionDate();
    entity.amount = purchase.amount().getValue();

    entity.persist();
    return toPurchase(entity);
  }

  @PrePersist
  private void prePersist() {
    entity.uuid = UUID.randomUUID();
  }
}
