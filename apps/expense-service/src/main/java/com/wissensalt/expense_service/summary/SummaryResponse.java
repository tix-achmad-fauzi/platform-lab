package com.wissensalt.expense_service.summary;

import java.util.List;

public record SummaryResponse(String month, List<Line> lines) {

}
