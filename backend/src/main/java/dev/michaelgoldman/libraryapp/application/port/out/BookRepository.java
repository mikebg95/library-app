package dev.michaelgoldman.libraryapp.application.port.out;

import dev.michaelgoldman.libraryapp.domain.model.BookId;

public interface BookRepository {

    boolean exists(BookId bookId);
}
