package com.wissensalt.expense_service.expense;

public class ExpenseNotFoundException extends RuntimeException {

  public ExpenseNotFoundException(long id) {
    super("Expense " + id + " not found");
  }
}
