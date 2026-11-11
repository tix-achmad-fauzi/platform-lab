# Expense Service – Execution Plan

## Context

A simple expense management service: Spring Boot 4.1.2, Java 25, Maven, PostgreSQL 17.2,
Liquibase-owned schema (not Hibernate). Starting point: bootstrap `pom.xml` (liquibase, webmvc,
actuator, postgresql, testcontainers), empty `Dockerfile`, `docker-compose.yaml` containing only
`services:`, minimal `application.yaml`.

## Decisions

- Monthly list: `GET /expenses?month=yyyy-MM` (a `/expenses/{month}` route would collide with
  `/expenses/{id}`).
- Persistence: Spring Data JPA with `ddl-auto=validate`; Liquibase owns the schema.
- Schema aligned to the request: `category varchar(50)`, `amount numeric(12,2) NOT NULL`; id is
  `Long` internally and `BigInteger` in the response.
- `GET /summary?month=yyyy-MM`, defaulting to the current month.
- Multi-stage Dockerfile (Maven + JDK 25 build stage, JRE 25 runtime stage).

## Steps

1. **pom.xml**: add `spring-boot-starter-data-jpa` and `spring-boot-starter-validation`; keep
   existing deps.
2. **application.yaml**
    - `server.port=8081`
    - Datasource configured so `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_PASSWORD` override via
      relaxed binding (local defaults in the file, no explicit placeholders)
    - `spring.datasource.hikari.connection-timeout=3000`
    - `spring.jpa.hibernate.ddl-auto=validate`
    - `spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml`
    - Actuator: `management.endpoint.health.probes.enabled=true`; expose `health`; `readiness`
      group = `readinessState,db`; `liveness` group = `livenessState` only
3. **Liquibase**: `db/changelog/db.changelog-master.yaml` and changeset `001-create-expense.yaml`
    - Table `expense`: `id` bigserial PK, `amount` numeric (12,2) not null, `currency` varchar (3)
      not null, `category` varchar (50) not null, `description` varchar (255), `spent_at` date not
      null, `created_at` timestamptz not null default now ()
    - Index `idx_expense_spent_at(spent_at)`
4. **Domain** (`com.wissensalt.expense_service`): `Expense` entity (`created_at` DB-generated, not
   insertable/updatable); `ExpenseRepository` with find-by-date-range ordered by `spent_at` desc and
   a JPQL summary (group by currency, category; `sum`, `count`).
5. **API**
    - Records: `CreateExpenseRequest`, `ExpenseResponse`, `SummaryResponse` / `Line` exactly as
      specified
    - `ExpenseService`
    - `ExpenseController`: `POST /expenses` → 201 + Location; `GET /expenses/{id}` → 404 if missing;
      `GET /expenses?month=`
    - `SummaryController`: `GET /summary?month=`
    - Currency normalised to upper-case; month parsed with `YearMonth`, invalid → 400
6. **Error handling**: `@RestControllerAdvice` returning `ProblemDetail` for validation (400), not
   found (404), bad month (400).
7. **docker-compose.yaml**: `postgres:17.2-alpine3.21`, env for db/user/password, port 5432, named
   volume, `pg_isready` healthcheck.
8. **Dockerfile**: build stage with JDK 25 + Maven wrapper (`./mvnw -DskipTests package`); runtime
   stage JRE 25, non-root user, `EXPOSE 8081`, `ENTRYPOINT ["java","-jar","app.jar"]`.
9. **Tests**: Testcontainers (`postgres:17.2-alpine3.21`) integration tests for
   create/get/list/summary, validation failures, Liquibase applying cleanly, and health groups
   (readiness includes `db`, liveness does not).

## Verification

- `./mvnw verify` passes (including Testcontainers tests).
- `docker compose up -d`; run the app with `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_PASSWORD`;
  `/actuator/health/liveness` and `/actuator/health/readiness` on :8081 → UP.
- Stop Postgres: readiness → DOWN (503), liveness stays UP.
- `curl` flow: POST → GET by id → list by month → summary.
- `docker build` succeeds and the image runs against the compose DB.
