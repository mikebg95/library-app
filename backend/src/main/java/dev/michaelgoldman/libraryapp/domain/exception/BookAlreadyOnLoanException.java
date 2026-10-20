package dev.michaelgoldman.libraryapp.domain.exception;

import dev.michaelgoldman.libraryapp.domain.model.BookId;

public class BookAlreadyOnLoanException extends RuntimeException {

    public BookAlreadyOnLoanException(BookId id) {
        super("Book " + id.value() + " is already on loan");
    }
}
