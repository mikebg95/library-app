package dev.michaelgoldman.libraryapp.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BookIdTest {

    @Test
    void acceptsPositiveValue() {
        assertThat(new BookId(1).value()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void rejectsZeroOrNegativeValue(long value) {
        assertThatThrownBy(() -> new BookId(value)).isInstanceOf(IllegalArgumentException.class);
    }
}
