package com.wissensalt.expense_service.expense;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.YearMonth;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

  private final ExpenseService service;

  public ExpenseController(ExpenseService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody CreateExpenseRequest request) {
    ExpenseResponse created = service.create(request);
    return ResponseEntity.created(URI.create("/expenses/" + created.id())).body(created);
  }

  @GetMapping("/{id}")
  public ExpenseResponse get(@PathVariable long id) {
    return service.get(id);
  }

  @GetMapping
  public List<ExpenseResponse> list(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
    return service.listByMonth(month);
  }
}
