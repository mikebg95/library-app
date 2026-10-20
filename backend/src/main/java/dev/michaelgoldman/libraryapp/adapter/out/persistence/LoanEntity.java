package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loan")
class LoanEntity {

    @Id
    private UUID id;

    private long memberId;
    private long bookId;
    private Instant borrowedAt;
    private Instant returnedAt;

    protected LoanEntity() {}

    LoanEntity(UUID id, long memberId, long bookId, Instant borrowedAt, Instant returnedAt) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.borrowedAt = borrowedAt;
        this.returnedAt = returnedAt;
    }

    UUID getId() {
        return id;
    }

    long getMemberId() {
        return memberId;
    }

    long getBookId() {
        return bookId;
    }

    Instant getBorrowedAt() {
        return borrowedAt;
    }

    Instant getReturnedAt() {
        return returnedAt;
    }
}
