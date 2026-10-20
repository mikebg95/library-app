package dev.michaelgoldman.libraryapp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.michaelgoldman.libraryapp.domain.exception.BookAlreadyOnLoanException;
import dev.michaelgoldman.libraryapp.domain.exception.BookNotFoundException;
import dev.michaelgoldman.libraryapp.domain.exception.LoanNotFoundException;
import dev.michaelgoldman.libraryapp.domain.exception.MaxLoansReachedException;
import dev.michaelgoldman.libraryapp.domain.exception.MemberNotFoundException;
import dev.michaelgoldman.libraryapp.domain.model.BookId;
import dev.michaelgoldman.libraryapp.domain.model.Loan;
import dev.michaelgoldman.libraryapp.domain.model.LoanId;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LoanServiceTest {

    private static final MemberId MEMBER = new MemberId(1);
    private static final BookId BOOK = new BookId(1);
    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");

    private final Set<MemberId> existingMembers = new HashSet<>(Set.of(MEMBER));
    private final Set<BookId> existingBooks = new HashSet<>(Set.of(BOOK));
    private final InMemoryLoanRepository loans = new InMemoryLoanRepository();

    private final LoanService service = new LoanService(
            existingMembers::contains, existingBooks::contains, loans, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void borrow_whenAllIsValid_returnLoanObject() {
        Loan loan = service.borrow(MEMBER, BOOK);

        assertThat(loan.memberId()).isEqualTo(MEMBER);
        assertThat(loan.bookId()).isEqualTo(BOOK);
        assertThat(loan.borrowedAt()).isEqualTo(NOW);
        assertThat(loan.isActive()).isTrue();
        assertThat(loans.findById(loan.id())).contains(loan);
    }

    @Test
    void borrow_whenMemberDoesNotExist_throwMemberNotFoundException() {
        existingMembers.clear();

        assertThatThrownBy(() -> service.borrow(MEMBER, BOOK)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void borrow_whenBookDoesNotExist_throwBookNotFoundException() {
        existingBooks.clear();

        assertThatThrownBy(() -> service.borrow(MEMBER, BOOK)).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void borrow_whenMemberHasThreeActiveLoans_throwMaxLoansReachedException() {
        loans.save(Loan.start(MEMBER, new BookId(2), NOW));
        loans.save(Loan.start(MEMBER, new BookId(3), NOW));
        loans.save(Loan.start(MEMBER, new BookId(4), NOW));

        assertThatThrownBy(() -> service.borrow(MEMBER, BOOK)).isInstanceOf(MaxLoansReachedException.class);
    }

    @Test
    void borrow_whenBookIsAlreadyBorrowed_throwBookAlreadyOnLoanException() {
        loans.save(Loan.start(new MemberId(2), BOOK, NOW));

        assertThatThrownBy(() -> service.borrow(MEMBER, BOOK)).isInstanceOf(BookAlreadyOnLoanException.class);
    }

    @Test
    void activeLoansOf_whenMemberExists_returnActiveLoans() {
        Loan loan = loans.save(Loan.start(MEMBER, BOOK, NOW));

        assertThat(service.activeLoansOf(MEMBER)).containsExactly(loan);
    }

    @Test
    void activeLoansOf_whenMemberDoesNotExist_throwMemberNotFoundException() {
        existingMembers.clear();

        assertThatThrownBy(() -> service.activeLoansOf(MEMBER)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void returnBook_whenLoanIsActive_returnReturnedLoan() {
        Loan loan = loans.save(Loan.start(MEMBER, BOOK, NOW));

        Loan returned = service.returnBook(loan.id());

        assertThat(returned.returnedAt()).isEqualTo(NOW);
        assertThat(returned.isActive()).isFalse();
    }

    @Test
    void returnBook_whenLoanDoesNotExist_throwLoanNotFoundException() {
        assertThatThrownBy(() -> service.returnBook(LoanId.generate())).isInstanceOf(LoanNotFoundException.class);
    }
}
