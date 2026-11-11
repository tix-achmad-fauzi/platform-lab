package com.wissensalt.expense_service.expense;

import com.wissensalt.expense_service.summary.Line;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  List<Expense> findBySpentAtBetweenOrderBySpentAtDescIdDesc(LocalDate from, LocalDate to);

  @Query("""
      select new com.wissensalt.expense_service.summary.Line(
          e.currency, e.category, sum(e.amount), count(e))
      from Expense e
      where e.spentAt between :from and :to
      group by e.currency, e.category
      order by e.currency, e.category
      """)
  List<Line> summarize(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
