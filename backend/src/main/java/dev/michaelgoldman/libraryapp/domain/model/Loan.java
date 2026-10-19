package dev.michaelgoldman.libraryapp.domain.model;

import dev.michaelgoldman.libraryapp.domain.exception.LoanAlreadyReturnedException;
import java.time.Instant;

public class Loan {

    private final LoanId id;
    private final MemberId memberId;
    private final BookId bookId;
    private final Instant borrowedAt;
    private Instant returnedAt;

    public Loan(LoanId id, MemberId memberId, BookId bookId, Instant borrowedAt, Instant returnedAt) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.borrowedAt = borrowedAt;
        this.returnedAt = returnedAt;
    }

    public static Loan start(MemberId memberId, BookId bookId, Instant now) {
        return new Loan(LoanId.generate(), memberId, bookId, now, null);
    }

    public void returnBook(Instant now) {
        if (!isActive()) {
            throw new LoanAlreadyReturnedException(id);
        }
        returnedAt = now;
    }

    public boolean isActive() {
        return returnedAt == null;
    }

    public LoanId id() {
        return id;
    }

    public MemberId memberId() {
        return memberId;
    }

    public BookId bookId() {
        return bookId;
    }

    public Instant borrowedAt() {
        return borrowedAt;
    }

    public Instant returnedAt() {
        return returnedAt;
    }
}
