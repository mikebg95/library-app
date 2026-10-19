package dev.michaelgoldman.libraryapp.domain.model;

import java.util.Objects;
import java.util.UUID;

public record LoanId(UUID value) {

    public LoanId {
        Objects.requireNonNull(value, "LoanId must not be null");
    }

    public static LoanId generate() {
        return new LoanId(UUID.randomUUID());
    }
}
