# 1. Hexagonal architecture with a framework-free core

- **Status:** Accepted
- **Date:** 2026-10-20

## Context

The business rules of this app are small but real: at most 3 active loans per member, one active loan per book, and no returning a loan twice. These rules should be easy to read and test, and they should not change when the web layer or the database changes.

In a typical Spring app, the service has `@Service`, `@Transactional` and other annotations, so the business logic depends on the framework.

## Decision

We use hexagonal architecture (ports and adapters):

- **The core** (`domain` and `application`) is plain Java. It contains the model (`Loan`, the ids, the domain exceptions), the ports (`LoanUseCases`, `LoanRepository`, `MemberRepository`, `BookRepository`) and `LoanService`. It has no Spring, JPA or Jackson.
- **Adapters** sit around the core. The web adapter (`LoanController`) calls the inbound port. The persistence adapters implement the outbound ports.
- **Dependencies point inward.** Adapters know the core; the core never knows the adapters.
- **`ApplicationConfig` wires everything.** Because `LoanService` has no `@Service`, a `@Bean` method creates it and registers it as `LoanUseCases`. This is the only place where Spring and the core meet.
- **ArchUnit enforces the rules.** `ArchitectureTest` fails the build if the core uses a framework or an adapter, or if DTOs or JPA entities leak out of their adapter.

## Consequences

- The business rules can be tested with `new LoanService(...)`, an in-memory repository and a fixed `Clock`: no Spring, no database, milliseconds per test.
- The database or the web layer can change without touching the core.
- Each layer has its own model (DTO, `Loan`, `LoanEntity`), so we write mapping code at the edges. With this few fields, that cost is small.
- Spring features that need annotations in the core, such as `@Transactional`, must be added from the outside (for example with a decorator in `config`). We have not done this yet.
