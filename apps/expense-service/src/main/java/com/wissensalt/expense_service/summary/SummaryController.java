package com.wissensalt.expense_service.summary;

import com.wissensalt.expense_service.expense.ExpenseService;
import java.time.YearMonth;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SummaryController {

  private final ExpenseService service;

  public SummaryController(ExpenseService service) {
    this.service = service;
  }

  @GetMapping("/summary")
  public SummaryResponse summary(
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
    YearMonth m = month != null ? month : YearMonth.now();
    return new SummaryResponse(m.toString(), service.summarize(m));
  }
}
