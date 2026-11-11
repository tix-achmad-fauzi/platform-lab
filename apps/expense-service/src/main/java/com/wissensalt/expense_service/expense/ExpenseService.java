package com.wissensalt.expense_service.expense;

import com.wissensalt.expense_service.summary.Line;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExpenseService {

  private final ExpenseRepository repository;

  public ExpenseService(ExpenseRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public ExpenseResponse create(CreateExpenseRequest request) {
    Expense saved = repository.saveAndFlush(new Expense(
        request.amount(),
        request.currency().toUpperCase(Locale.ROOT),
        request.category(),
        request.description(),
        request.spentAt()));
    return ExpenseResponse.from(saved);
  }

  public ExpenseResponse get(long id) {
    return repository.findById(id).map(ExpenseResponse::from)
        .orElseThrow(() -> new ExpenseNotFoundException(id));
  }

  public List<ExpenseResponse> listByMonth(YearMonth month) {
    return repository
        .findBySpentAtBetweenOrderBySpentAtDescIdDesc(month.atDay(1), month.atEndOfMonth())
        .stream().map(ExpenseResponse::from).toList();
  }

  public List<Line> summarize(YearMonth month) {
    return repository.summarize(month.atDay(1), month.atEndOfMonth());
  }
}
