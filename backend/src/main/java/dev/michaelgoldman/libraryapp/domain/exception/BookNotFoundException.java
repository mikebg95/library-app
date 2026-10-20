package dev.michaelgoldman.libraryapp.domain.exception;

import dev.michaelgoldman.libraryapp.domain.model.BookId;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(BookId id) {
        super("Book " + id.value() + " not found");
    }
}
