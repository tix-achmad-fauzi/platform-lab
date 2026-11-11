package com.wissensalt.expense_service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ExpenseServiceApplicationTests {

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17.2-alpine3.21");

  @Autowired
  MockMvc mvc;

  private static String body(String amount, String currency, String category, LocalDate date) {
    return """
        {"amount": %s, "currency": "%s", "category": "%s", "description": "d", "spentAt": "%s"}
        """.formatted(amount, currency, category, date);
  }

  @Test
  void createGetListAndSummary() throws Exception {
    LocalDate date = LocalDate.of(2024, 3, 15);
    String location = mvc.perform(post("/expenses").contentType(MediaType.APPLICATION_JSON)
            .content(body("10.50", "idr", "food", date)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.currency").value("IDR"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andReturn().getResponse().getHeader("Location");
    mvc.perform(post("/expenses").contentType(MediaType.APPLICATION_JSON)
            .content(body("4.50", "IDR", "food", date)))
        .andExpect(status().isCreated());

    mvc.perform(get(location)).andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(10.50))
        .andExpect(jsonPath("$.spentAt").value("2024-03-15"));

    mvc.perform(get("/expenses").param("month", "2024-03")).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
    mvc.perform(get("/expenses").param("month", "2024-04")).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

    mvc.perform(get("/summary").param("month", "2024-03")).andExpect(status().isOk())
        .andExpect(jsonPath("$.month").value("2024-03"))
        .andExpect(jsonPath("$.lines[0].currency").value("IDR"))
        .andExpect(jsonPath("$.lines[0].category").value("food"))
        .andExpect(jsonPath("$.lines[0].total").value(15.00))
        .andExpect(jsonPath("$.lines[0].count").value(2));
  }

  @Test
  void validationAndErrors() throws Exception {
    mvc.perform(post("/expenses").contentType(MediaType.APPLICATION_JSON)
            .content(body("-1", "ID", "food", LocalDate.now().plusDays(1))))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/expenses/999999")).andExpect(status().isNotFound());
    mvc.perform(get("/expenses").param("month", "2024-13")).andExpect(status().isBadRequest());
  }

  @Test
  void healthProbes() throws Exception {
    mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
    mvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
    mvc.perform(get("/actuator/health/readiness")).andExpect(header().exists("Content-Type"));
  }
}
