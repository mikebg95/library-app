package dev.michaelgoldman.libraryapp.application.service;

import dev.michaelgoldman.libraryapp.application.port.out.LoanRepository;
import dev.michaelgoldman.libraryapp.domain.model.BookId;
import dev.michaelgoldman.libraryapp.domain.model.Loan;
import dev.michaelgoldman.libraryapp.domain.model.LoanId;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class InMemoryLoanRepository implements LoanRepository {

    private final Map<LoanId, Loan> store = new HashMap<>();

    @Override
    public Optional<Loan> findById(LoanId loanId) {
        return Optional.ofNullable(store.get(loanId));
    }

    @Override
    public List<Loan> findActiveByMember(MemberId memberId) {
        return store.values().stream()
                .filter(loan -> loan.isActive() && loan.memberId().equals(memberId))
                .toList();
    }

    @Override
    public boolean isOnLoan(BookId bookId) {
        return store.values().stream()
                .anyMatch(loan -> loan.isActive() && loan.bookId().equals(bookId));
    }

    @Override
    public Loan save(Loan loan) {
        store.put(loan.id(), loan);
        return loan;
    }
}
