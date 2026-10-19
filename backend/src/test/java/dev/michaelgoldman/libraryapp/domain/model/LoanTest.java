package dev.michaelgoldman.libraryapp.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.michaelgoldman.libraryapp.domain.exception.LoanAlreadyReturnedException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class LoanTest {

    private static final MemberId MEMBER = new MemberId(1);
    private static final BookId BOOK = new BookId(1);
    private static final Instant BORROWED_AT = Instant.parse("2026-10-19T10:00:00Z");
    private static final Instant RETURNED_AT = Instant.parse("2026-10-26T10:00:00Z");

    @Test
    void startCreatesActiveLoan() {
        Loan loan = Loan.start(MEMBER, BOOK, BORROWED_AT);

        assertThat(loan.id()).isNotNull();
        assertThat(loan.memberId()).isEqualTo(MEMBER);
        assertThat(loan.bookId()).isEqualTo(BOOK);
        assertThat(loan.borrowedAt()).isEqualTo(BORROWED_AT);
        assertThat(loan.returnedAt()).isNull();
        assertThat(loan.isActive()).isTrue();
    }

    @Test
    void returnBookEndsLoan() {
        Loan loan = Loan.start(MEMBER, BOOK, BORROWED_AT);

        loan.returnBook(RETURNED_AT);

        assertThat(loan.returnedAt()).isEqualTo(RETURNED_AT);
        assertThat(loan.isActive()).isFalse();
    }

    @Test
    void returningTwiceIsRejected() {
        Loan loan = Loan.start(MEMBER, BOOK, BORROWED_AT);
        loan.returnBook(RETURNED_AT);

        assertThatThrownBy(() -> loan.returnBook(RETURNED_AT)).isInstanceOf(LoanAlreadyReturnedException.class);
    }
}
