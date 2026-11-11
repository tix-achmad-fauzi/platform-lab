package com.wissensalt.expense_service.expense;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;

public record ExpenseResponse(BigInteger id, BigDecimal amount, String currency, String category,
                              String description, LocalDate spentAt, Instant createdAt) {

  static ExpenseResponse from(Expense e) {
    return new ExpenseResponse(BigInteger.valueOf(e.getId()), e.getAmount(), e.getCurrency(),
        e.getCategory(), e.getDescription(), e.getSpentAt(), e.getCreatedAt());
  }
}
