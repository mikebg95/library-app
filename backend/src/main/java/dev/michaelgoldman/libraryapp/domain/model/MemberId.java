package dev.michaelgoldman.libraryapp.domain.model;

public record MemberId(long value) {

    public MemberId {
        if (value <= 0) {
            throw new IllegalArgumentException("MemberId must be positive");
        }
    }
}
