package com.wissensalt.expense_service.summary;

import java.math.BigDecimal;

public record Line(String currency, String category, BigDecimal total, long count) {

}
