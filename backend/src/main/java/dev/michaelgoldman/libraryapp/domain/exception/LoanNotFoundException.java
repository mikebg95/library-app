package dev.michaelgoldman.libraryapp.domain.exception;

import dev.michaelgoldman.libraryapp.domain.model.LoanId;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(LoanId id) {
        super("Loan " + id.value() + " not found");
    }
}
