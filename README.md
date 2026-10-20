# Library app

A small library backend for **librarians at the front desk**: lend a book to a member, take it back, and see a member's active loans. Built as a technical assessment for Flock, around two of their architecture principles: **specified contracts** and **domain isolation**.

It is deliberately small, so every line can be explained and defended.

## Requirements

- Java 25
- Docker (PostgreSQL runs in a Testcontainers container, for the tests and when running locally)

## Run it

```bash
cd backend
./mvnw spring-boot:test-run
```

This starts the app on `http://localhost:8080` with a PostgreSQL 18 container. Flyway creates the schema and seeds 3 members (ids 1–3) and 5 books (ids 1–5).

```bash
# Lend book 1 to member 1
curl -i -X POST localhost:8080/api/v1/loans \
  -H 'Content-Type: application/json' \
  -d '{"memberId": 1, "bookId": 1}'

# Active loans of member 1
curl localhost:8080/api/v1/members/1/loans

# Return a loan (use the id from the Location header)
curl -X POST localhost:8080/api/v1/loans/{loanId}/return
```

## Front-desk screen

A minimal Vue 3 screen in `frontend/`: pick a member, see their active loans, lend a book, return a loan. It has no business rules; it shows what the backend decides, including the `detail` of every error.

With the backend running:

```bash
cd frontend
npm install
npm run dev
```

Then open `http://localhost:5173`. Vite proxies `/api` to the backend on port 8080, so no CORS setup is needed.

The TypeScript types come from the same contract as the backend: `npm run generate` (run automatically before `dev` and `build`) uses `openapi-typescript` to turn `openapi.yaml` into `src/api/schema.d.ts`, and `openapi-fetch` uses those types for every call. If the contract changes, `npm run build` fails with a type error. The generated file is not committed.

## Test it

```bash
cd backend
./mvnw verify
```

Runs all 42 tests plus the Spotless format check. CI (GitHub Actions) runs this on every push, and also builds the frontend, which type-checks it against the contract.

## API

The contract is [`backend/src/main/resources/openapi.yaml`](backend/src/main/resources/openapi.yaml) (OpenAPI 3.0.3). It is **contract-first**: the build generates the `LoansApi` interface and DTOs from it, and the controller implements that interface, so it cannot drift from the contract without failing to compile.

| Method | Path | Success | Errors |
|---|---|---|---|
| `POST` | `/api/v1/loans` with `{ memberId, bookId }` | `201` + `Location` + loan | `400`, `404`, `409` |
| `POST` | `/api/v1/loans/{loanId}/return` | `200` + loan | `400`, `404`, `409` |
| `GET` | `/api/v1/members/{memberId}/loans` | `200` + active loans | `400`, `404` |

Business rules:

- A member can have at most **3 active loans** → `409`
- A book is on loan to **one member at a time** → `409`
- A loan cannot be returned twice → `409`
- Unknown member, book or loan → `404`
- Invalid input → `400`

Every error is returned as `application/problem+json` (RFC 9457 `ProblemDetail`).

## Architecture

Hexagonal (ports and adapters), in one Maven project under `dev.michaelgoldman.libraryapp`:

```
domain/            Loan, LoanId, MemberId, BookId, domain exceptions      ┐ the core:
application/       port/in (LoanUseCases), port/out (repositories),       │ plain Java,
                   service (LoanService)                                  ┘ no Spring
adapter/in/web/    LoanController (implements generated LoansApi), ErrorHandler
adapter/out/persistence/   JPA entities, Spring Data repositories, persistence adapters
config/            ApplicationConfig: wires LoanService as the LoanUseCases bean
```

- **The core is framework-free.** `LoanService` has no `@Service`; `ApplicationConfig` creates it with a `@Bean` method. This is the only place where Spring and the core meet.
- **Dependencies point inward.** Adapters depend on the core's ports, never the other way around.
- **Separate models per layer.** The API (DTOs), the domain (`Loan`) and the database (`LoanEntity`) each have their own model, mapped at the edges.
- **Enforced, not just agreed.** `ArchitectureTest` (ArchUnit) fails the build if the core uses a framework or an adapter, or if DTOs or JPA entities leak out of their adapter.

The database guarantees one active loan per book with a partial unique index (`UNIQUE (book_id) WHERE returned_at IS NULL`).

## Tests

| Level | Tests | What they check |
|---|---|---|
| Domain | `LoanTest`, `*IdTest`, `LoanServiceTest` | Business rules, with an in-memory repository and a fixed `Clock` |
| Web | `LoanControllerTest`, `ErrorHandlerTest` | HTTP ↔ use case, JSON shape, status codes and `ProblemDetail` (`@WebMvcTest`) |
| Persistence | `*PersistenceAdapterTest` | Mapping and queries against real PostgreSQL (`@DataJpaTest` + Testcontainers) |
| Architecture | `ArchitectureTest` | The hexagonal rules above |
| End-to-end | `LoanEndToEndTest` | Borrow → list → return → list, over real HTTP with the real database |

## Out of scope

Discussed, not built: authentication (librarians act on behalf of a member, so `memberId` is in the request body), transactions around the use cases, a second bounded context, a v2 of the contract, domain events, renewals and fines, and frontend tests (the screen has no logic; the type check is its safety net).

## Docs

- [`docs/diagrams/architecture/bibliotheek-architectuur.html`](docs/diagrams/architecture/bibliotheek-architectuur.html): interactive, zoomable architecture diagram (Dutch)
- [`docs/diagrams/db/`](docs/diagrams/db/): database diagram
- [`docs/adr/`](docs/adr/): architecture decision records

## Stack

Java 25 · Spring Boot 4.1 (Spring Framework 7, Hibernate 7, Jackson 3) · PostgreSQL 18 · Flyway · OpenAPI Generator · Testcontainers 2 · ArchUnit · Spotless (Palantir Java Format)

Frontend: Vue 3 · Vite · TypeScript · openapi-typescript · openapi-fetch
