package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import dev.michaelgoldman.libraryapp.TestcontainersConfiguration;
import dev.michaelgoldman.libraryapp.domain.model.BookId;
import dev.michaelgoldman.libraryapp.domain.model.Loan;
import dev.michaelgoldman.libraryapp.domain.model.LoanId;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({TestcontainersConfiguration.class, LoanPersistenceAdapter.class})
class LoanPersistenceAdapterTest {

    private static final MemberId MEMBER = new MemberId(1);
    private static final BookId BOOK = new BookId(1);
    private static final Instant BORROWED_AT = Instant.parse("2026-10-19T10:00:00Z");
    private static final Instant RETURNED_AT = Instant.parse("2026-10-26T10:00:00Z");

    @Autowired
    private LoanPersistenceAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findById_whenLoanExists_returnLoan() {
        Loan loan = adapter.save(Loan.start(MEMBER, BOOK, BORROWED_AT));
        entityManager.flush();
        entityManager.clear();

        assertThat(adapter.findById(loan.id())).get().usingRecursiveComparison().isEqualTo(loan);
    }

    @Test
    void findById_whenLoanIsUnknown_returnEmpty() {
        assertThat(adapter.findById(LoanId.generate())).isEmpty();
    }

    @Test
    void findActiveByMember_returnOnlyActiveLoansOfThatMember() {
        Loan active = adapter.save(Loan.start(MEMBER, BOOK, BORROWED_AT));
        Loan returned = Loan.start(MEMBER, new BookId(2), BORROWED_AT);
        returned.returnBook(RETURNED_AT);
        adapter.save(returned);
        adapter.save(Loan.start(new MemberId(2), new BookId(3), BORROWED_AT));

        assertThat(adapter.findActiveByMember(MEMBER)).extracting(Loan::id).containsExactly(active.id());
    }

    @Test
    void isOnLoan_whenBookHasActiveLoan_returnTrue() {
        adapter.save(Loan.start(MEMBER, BOOK, BORROWED_AT));

        assertThat(adapter.isOnLoan(BOOK)).isTrue();
    }

    @Test
    void isOnLoan_whenLoanIsReturned_returnFalse() {
        Loan loan = Loan.start(MEMBER, BOOK, BORROWED_AT);
        loan.returnBook(RETURNED_AT);
        adapter.save(loan);

        assertThat(adapter.isOnLoan(BOOK)).isFalse();
    }

    @Test
    void save_whenLoanIsReturned_updateReturnedAt() {
        Loan saved = adapter.save(Loan.start(MEMBER, BOOK, BORROWED_AT));
        entityManager.flush();
        entityManager.clear();

        Loan loan = adapter.findById(saved.id()).orElseThrow();
        loan.returnBook(RETURNED_AT);
        adapter.save(loan);
        entityManager.flush();
        entityManager.clear();

        assertThat(adapter.findById(saved.id()))
                .get()
                .extracting(Loan::returnedAt)
                .isEqualTo(RETURNED_AT);
    }
}
