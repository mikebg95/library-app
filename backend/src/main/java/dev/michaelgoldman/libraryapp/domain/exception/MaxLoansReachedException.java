package dev.michaelgoldman.libraryapp.domain.exception;

import dev.michaelgoldman.libraryapp.domain.model.MemberId;

public class MaxLoansReachedException extends RuntimeException {

    public MaxLoansReachedException(MemberId id) {
        super("Member " + id.value() + " has reached the maximum number of active loans");
    }
}
