package dev.michaelgoldman.libraryapp.domain.exception;

import dev.michaelgoldman.libraryapp.domain.model.LoanId;

public class LoanAlreadyReturnedException extends RuntimeException {

    public LoanAlreadyReturnedException(LoanId id) {
        super("Loan " + id.value() + " is already returned");
    }
}
