# 2. Contract-first API with OpenAPI

- **Status:** Accepted
- **Date:** 2026-10-20

## Context

The API is a promise to its consumers, such as the front-desk screen. That promise should be explicit, versioned and stable. If the code is the only source of truth, a small refactoring can change the API without anyone noticing.

## Decision

The contract comes first, and the code follows it:

- **The contract is `backend/src/main/resources/openapi.yaml`** (OpenAPI 3.0.3). It describes the three endpoints, the request and response shapes, and every error response.
- **The build generates code from it.** The OpenAPI Generator Maven plugin creates the `LoansApi` interface and the DTOs (`BorrowRequestDto`, `LoanDto`, `ProblemDto`) in `target/generated-sources`. This code is not committed.
- **`LoanController` implements `LoansApi`.** With `skipDefaultInterface`, a missing or different endpoint does not compile.
- **The API is versioned** with `/api/v1` in the path and `info.version` in the contract. A breaking change would become `/api/v2`, running next to v1 until v1 is retired.
- **Tests check the wire format.** Controller tests send and compare raw JSON strings, so a renamed field or a changed date format fails the test.

## Consequences

- The controller cannot drift from the contract without a compile error.
- Consumers can read the contract without reading Java code, and could generate their own client or types from it.
- The generated DTOs differ from the domain model, so the controller maps between them.
- The contract's `servers: /api/v1` is not part of the generated code, so `LoanController` sets the prefix itself with `@RequestMapping("/api/v1")`.
- We use OpenAPI 3.0.3 because the generator's 3.1 support is still beta.
