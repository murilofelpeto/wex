package com.felpeto.purchase.repository;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Entity
public class PurchaseEntity extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  public Long id;

  @Column(columnDefinition = "BINARY(16)", nullable = false, unique = true)
  public UUID uuid;

  @Column(name = "amount", scale = 2, nullable = false)
  public BigDecimal amount;

  @Column(name = "transaction_date", nullable = false)
  public LocalDate transactionDate;

  @Column(name = "description", nullable = false, length = 50)
  public String description;

  public static Optional<PurchaseEntity> findByUuid(final UUID uuid) {
    return find("uuid", uuid).firstResultOptional();
  }
}
