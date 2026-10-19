package dev.michaelgoldman.libraryapp.domain.model;

public record BookId(long value) {

    public BookId {
        if (value <= 0) {
            throw new IllegalArgumentException("BookId must be positive");
        }
    }
}
