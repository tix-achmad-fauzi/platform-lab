package com.wissensalt.expense_service.expense;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseRequest(
    @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal amount,
    @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency,
    @NotBlank @Size(max = 50) String category,
    @Size(max = 255) String description,
    @NotNull @PastOrPresent LocalDate spentAt) {

}
