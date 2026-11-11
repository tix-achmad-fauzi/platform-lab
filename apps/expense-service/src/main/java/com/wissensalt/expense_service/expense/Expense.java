package com.wissensalt.expense_service.expense;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "expense")
public class Expense {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Column(nullable = false, length = 50)
  private String category;

  @Column(length = 255)
  private String description;

  @Column(name = "spent_at", nullable = false)
  private LocalDate spentAt;

  @Generated(event = EventType.INSERT)
  @Column(name = "created_at", insertable = false, updatable = false)
  private Instant createdAt;

  protected Expense() {
  }

  public Expense(BigDecimal amount, String currency, String category, String description,
      LocalDate spentAt) {
    this.amount = amount;
    this.currency = currency;
    this.category = category;
    this.description = description;
    this.spentAt = spentAt;
  }

  public Long getId() {
    return id;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public String getCategory() {
    return category;
  }

  public String getDescription() {
    return description;
  }

  public LocalDate getSpentAt() {
    return spentAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
