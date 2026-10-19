package dev.michaelgoldman.libraryapp.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class LoanIdTest {

    @Test
    void rejectsNullValue() {
        assertThatThrownBy(() -> new LoanId(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void generatesUniqueIds() {
        assertThat(LoanId.generate()).isNotEqualTo(LoanId.generate());
    }
}
