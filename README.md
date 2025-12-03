Expense Tracker Backend (Spring Boot)

Overview
This is a Spring Boot REST API that provides CRUD for expenses and scopes all data per client via the X-Client-Id header. It uses PostgreSQL, Flyway migrations, and is containerized with Docker. It is tailored for local development with Docker Compose and deployment on Render with a managed PostgreSQL instance.

Tech
- Java 25, Spring Boot 4
- Spring Web, Spring Data JPA, Validation
- PostgreSQL, Flyway
- Dockerfile + Docker Compose

API (base path: /api/expenses)
- Headers: X-Client-Id (required on every request)
- GET /api/expenses – list expenses
- GET /api/expenses/{id} – get one expense
- POST /api/expenses – create expense { title, amount, date(YYYY-MM-DD) }
- PUT /api/expenses/{id} – update expense (same body)
- DELETE /api/expenses/{id} – delete expense
Errors: 400 validation_error, 404 not_found

Run locally with Docker Compose
1) docker compose up --build
2) API: http://localhost:8080

Run locally without Docker
Prereqs: JDK 25 and PostgreSQL.
Env vars (defaults):
- SPRING_PROFILES_ACTIVE=dev
- SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/expense_db
- SPRING_DATASOURCE_USERNAME=postgres
- SPRING_DATASOURCE_PASSWORD=postgres
Run: ./mvnw spring-boot:run

Notes
- Flyway migration V1__create_expenses.sql creates the expenses table.
- CORS is permissive for development (see CorsConfig). Restrict in production.
