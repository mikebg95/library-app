package dev.michaelgoldman.libraryapp.application.port.out;

import dev.michaelgoldman.libraryapp.domain.model.BookId;
import dev.michaelgoldman.libraryapp.domain.model.Loan;
import dev.michaelgoldman.libraryapp.domain.model.LoanId;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    Optional<Loan> findById(LoanId loanId);

    List<Loan> findActiveByMember(MemberId memberId);

    boolean isOnLoan(BookId bookId);

    Loan save(Loan loan);
}
