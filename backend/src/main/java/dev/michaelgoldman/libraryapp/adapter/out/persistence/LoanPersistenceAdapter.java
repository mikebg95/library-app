package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import dev.michaelgoldman.libraryapp.application.port.out.LoanRepository;
import dev.michaelgoldman.libraryapp.domain.model.BookId;
import dev.michaelgoldman.libraryapp.domain.model.Loan;
import dev.michaelgoldman.libraryapp.domain.model.LoanId;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class LoanPersistenceAdapter implements LoanRepository {

    private final LoanJpaRepository jpa;

    LoanPersistenceAdapter(LoanJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Loan> findById(LoanId loanId) {
        return jpa.findById(loanId.value()).map(LoanPersistenceAdapter::toDomain);
    }

    @Override
    public List<Loan> findActiveByMember(MemberId memberId) {
        return jpa.findByMemberIdAndReturnedAtIsNull(memberId.value()).stream()
                .map(LoanPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public boolean isOnLoan(BookId bookId) {
        return jpa.existsByBookIdAndReturnedAtIsNull(bookId.value());
    }

    @Override
    public Loan save(Loan loan) {
        return toDomain(jpa.save(toEntity(loan)));
    }

    private static LoanEntity toEntity(Loan loan) {
        return new LoanEntity(
                loan.id().value(),
                loan.memberId().value(),
                loan.bookId().value(),
                loan.borrowedAt(),
                loan.returnedAt());
    }

    private static Loan toDomain(LoanEntity entity) {
        return new Loan(
                new LoanId(entity.getId()),
                new MemberId(entity.getMemberId()),
                new BookId(entity.getBookId()),
                entity.getBorrowedAt(),
                entity.getReturnedAt());
    }
}
