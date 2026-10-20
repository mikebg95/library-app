package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import dev.michaelgoldman.libraryapp.application.port.out.BookRepository;
import dev.michaelgoldman.libraryapp.domain.model.BookId;
import org.springframework.stereotype.Component;

@Component
class BookPersistenceAdapter implements BookRepository {

    private final BookJpaRepository jpa;

    BookPersistenceAdapter(BookJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean exists(BookId bookId) {
        return jpa.existsById(bookId.value());
    }
}
