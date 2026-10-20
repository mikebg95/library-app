# 3. One error format with ProblemDetail

- **Status:** Accepted
- **Date:** 2026-10-20

## Context

Errors are part of the API contract. A consumer needs to know which errors can happen and what they look like. Without extra work, the core's domain exceptions would reach the client as a `500`, and Spring's own errors (such as a missing field) would return a `400` with an empty body. That gives clients several different error shapes.

## Decision

Every error is returned as `application/problem+json`, using Spring's `ProblemDetail` (RFC 9457). The contract describes this as the `Problem` schema.

- **Each domain error is its own exception class** with a name in domain language, such as `MaxLoansReachedException`. The core knows nothing about HTTP.
- **`ErrorHandler` (`@RestControllerAdvice`) maps them to status codes:**
  - `404` for `MemberNotFoundException`, `BookNotFoundException` and `LoanNotFoundException`
  - `409` for `BookAlreadyOnLoanException`, `MaxLoansReachedException` and `LoanAlreadyReturnedException`
- **`ErrorHandler` extends `ResponseEntityExceptionHandler`**, so Spring's own errors (invalid input, invalid JSON, wrong types) also become a `ProblemDetail`, with status `400`.

## Consequences

- Every error has the same shape, whether it comes from the domain or from Spring.
- The controller has no try/catch. All error mapping lives in one class.
- A new domain exception must be added to `ErrorHandler`; otherwise it becomes a `500`. Adding a new error code to the API is a contract change.
- Errors that are not mapped still become a `500`. One known case is two desks lending the same book at the same moment: the database index blocks the second loan, but that error is not yet translated to a `409`.
