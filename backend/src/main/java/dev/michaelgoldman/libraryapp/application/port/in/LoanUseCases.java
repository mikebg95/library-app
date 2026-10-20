package dev.michaelgoldman.libraryapp.application.port.in;

import dev.michaelgoldman.libraryapp.domain.model.BookId;
import dev.michaelgoldman.libraryapp.domain.model.Loan;
import dev.michaelgoldman.libraryapp.domain.model.LoanId;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import java.util.List;

public interface LoanUseCases {

    Loan borrow(MemberId memberId, BookId bookId);

    Loan returnBook(LoanId loanId);

    List<Loan> activeLoansOf(MemberId memberId);
}
