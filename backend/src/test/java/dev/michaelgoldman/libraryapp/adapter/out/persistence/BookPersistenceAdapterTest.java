package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import dev.michaelgoldman.libraryapp.TestcontainersConfiguration;
import dev.michaelgoldman.libraryapp.domain.model.BookId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({TestcontainersConfiguration.class, BookPersistenceAdapter.class})
class BookPersistenceAdapterTest {

    @Autowired
    private BookPersistenceAdapter adapter;

    @Test
    void exists_whenBookIsSeeded_returnTrue() {
        assertThat(adapter.exists(new BookId(1))).isTrue();
    }

    @Test
    void exists_whenBookIsUnknown_returnFalse() {
        assertThat(adapter.exists(new BookId(999))).isFalse();
    }
}
