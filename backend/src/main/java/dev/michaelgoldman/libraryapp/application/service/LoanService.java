package dev.michaelgoldman.libraryapp.application.service;

import dev.michaelgoldman.libraryapp.application.port.in.LoanUseCases;
import dev.michaelgoldman.libraryapp.application.port.out.BookRepository;
import dev.michaelgoldman.libraryapp.application.port.out.LoanRepository;
import dev.michaelgoldman.libraryapp.application.port.out.MemberRepository;
import dev.michaelgoldman.libraryapp.domain.exception.BookAlreadyOnLoanException;
import dev.michaelgoldman.libraryapp.domain.exception.BookNotFoundException;
import dev.michaelgoldman.libraryapp.domain.exception.MaxLoansReachedException;
import dev.michaelgoldman.libraryapp.domain.exception.MemberNotFoundException;
import dev.michaelgoldman.libraryapp.domain.model.BookId;
import dev.michaelgoldman.libraryapp.domain.model.Loan;
import dev.michaelgoldman.libraryapp.domain.model.LoanId;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import java.time.Clock;
import java.util.List;

public class LoanService implements LoanUseCases {

    private static final int MAX_ACTIVE_LOANS = 3;

    private final MemberRepository members;
    private final BookRepository books;
    private final LoanRepository loans;
    private final Clock clock;

    public LoanService(MemberRepository members, BookRepository books, LoanRepository loans, Clock clock) {
        this.members = members;
        this.books = books;
        this.loans = loans;
        this.clock = clock;
    }

    @Override
    public Loan borrow(MemberId memberId, BookId bookId) {
        if (!members.exists(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        if (!books.exists(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        if (loans.findActiveByMember(memberId).size() >= MAX_ACTIVE_LOANS) {
            throw new MaxLoansReachedException(memberId);
        }
        if (loans.isOnLoan(bookId)) {
            throw new BookAlreadyOnLoanException(bookId);
        }
        return loans.save(Loan.start(memberId, bookId, clock.instant()));
    }

    @Override
    public Loan returnBook(LoanId loanId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Loan> activeLoansOf(MemberId memberId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
