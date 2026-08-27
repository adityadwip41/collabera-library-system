# Library System API

Production-minded REST API for registering borrowers and physical book copies, then borrowing and returning a specific copy.

## Technology

- Java 17, Spring Boot, Spring Web, Validation, and Data JPA
- Maven Wrapper for repeatable dependency and build management
- Flyway database migrations
- PostgreSQL in production; H2 in PostgreSQL compatibility mode for fast local development and tests
- JUnit 5, Mockito, MockMvc, Testcontainers, and JaCoCo
- OpenAPI/Swagger, Docker Compose, and Kubernetes

## Database choice

PostgreSQL is the production database because borrow/return is transactional and needs reliable locking and uniqueness under concurrent requests. The database constraint on `loans.book_id`, combined with a pessimistic book-row lock, guarantees that one physical copy cannot have two active borrowers. H2 is used only for a zero-setup local profile and fast automated tests.

The schema separates bibliographic metadata (`book_catalog`, keyed by ISBN) from physical copies (`books`, each with its own ID). This enforces that one ISBN maps to exactly one title/author while allowing any number of copies.

## Run

Prerequisite: Java 17.

```bash
./mvnw spring-boot:run
```

The default `local` profile starts an in-memory H2 database. Swagger UI is available at `http://localhost:8080/swagger-ui.html` and health at `http://localhost:8080/actuator/health`.

To run the production-style stack:

```bash
docker compose up --build
```

Production configuration is externalized with `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and optional `DB_POOL_SIZE`.

## API

### Register a borrower

```http
POST /api/v1/borrowers
Content-Type: application/json

{"name":"Alice Johnson","email":"alice@example.com"}
```

### Register one book copy

```http
POST /api/v1/books
Content-Type: application/json

{"isbn":"9780132350884","title":"Clean Code","author":"Robert C. Martin"}
```

Repeating this request creates another physical copy with a different ID. Reusing the ISBN with different metadata returns `409 Conflict`.

### List books

```http
GET /api/v1/books?page=0&size=20&sortBy=title&direction=ASC
```

Allowed sort fields are `id`, `isbn`, `title`, `author`, and `createdAt`. Page size is limited to 1–100. Each result includes its current `available` state.

### Borrow and return

```http
POST   /api/v1/borrowers/1/borrowed-books/2
DELETE /api/v1/borrowers/1/borrowed-books/2
```

Success responses contain the loan, borrower, book, timestamp, and status. Validation and business errors use RFC 9457 Problem Details JSON with appropriate `400`, `404`, or `409` status codes.

An importable Postman collection is provided at `postman/Library-System.postman_collection.json`.

## Test and coverage

```bash
./mvnw clean verify
```

The HTML coverage report is generated at `target/site/jacoco/index.html`. Tests cover request validation, ISBN consistency, multiple-copy registration, borrow/return, single-active-borrower enforcement, and service error paths. A Testcontainers test also applies the Flyway migration to PostgreSQL 17 when Docker is available; it is automatically skipped on hosts without a working Docker engine.

## Assumptions

- Email uniquely identifies a borrower and is stored lowercase; duplicate registration returns `409`.
- ISBN input is a normalized sequence of 10 or 13 digits. ISBN checksum validation is outside the stated scope.
- Title and author comparisons are exact after trimming surrounding whitespace.
- A borrower may hold multiple different copies, with no borrowing limit or due date.
- Only the borrower holding a copy may return it.
- `loans` represents active loans. Completed-loan history is outside scope because the assessment only asks to borrow and return.
- Authentication, authorization, reservations, fines, notifications, and borrower deletion are outside scope.
- The book list is paginated to remain efficient as data grows; defaults are page 0 and size 20.

## 12-Factor alignment

Dependencies are explicitly declared, configuration and backing-service credentials come from the environment, the application is stateless, logs go to stdout, database changes run as migrations, and the same immutable container can run across environments. Kubernetes probes and graceful shutdown support disposable instances.

## Project structure

```text
src/main/java/com/collabera/library
├── api          REST controllers, DTOs, and error mapping
├── config       OpenAPI configuration
├── domain       JPA domain model
├── repository   persistence interfaces
└── service      transactions and business rules
```
