package dev.michaelgoldman.libraryapp.adapter.in.web;

import dev.michaelgoldman.libraryapp.adapter.in.web.api.LoansApi;
import dev.michaelgoldman.libraryapp.adapter.in.web.api.model.BorrowRequestDto;
import dev.michaelgoldman.libraryapp.adapter.in.web.api.model.LoanDto;
import dev.michaelgoldman.libraryapp.application.port.in.LoanUseCases;
import dev.michaelgoldman.libraryapp.domain.model.BookId;
import dev.michaelgoldman.libraryapp.domain.model.Loan;
import dev.michaelgoldman.libraryapp.domain.model.LoanId;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1")
class LoanController implements LoansApi {

    private final LoanUseCases loanUseCases;

    LoanController(LoanUseCases loanUseCases) {
        this.loanUseCases = loanUseCases;
    }

    @Override
    public ResponseEntity<LoanDto> borrowBook(BorrowRequestDto request) {
        Loan loan = loanUseCases.borrow(new MemberId(request.getMemberId()), new BookId(request.getBookId()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(loan.id().value())
                .toUri();
        return ResponseEntity.created(location).body(toDto(loan));
    }

    @Override
    public ResponseEntity<LoanDto> returnBook(UUID loanId) {
        return ResponseEntity.ok(toDto(loanUseCases.returnBook(new LoanId(loanId))));
    }

    @Override
    public ResponseEntity<List<LoanDto>> getActiveLoans(Long memberId) {
        return ResponseEntity.ok(loanUseCases.activeLoansOf(new MemberId(memberId)).stream()
                .map(LoanController::toDto)
                .toList());
    }

    private static LoanDto toDto(Loan loan) {
        return new LoanDto(
                        loan.id().value(),
                        loan.memberId().value(),
                        loan.bookId().value(),
                        toOffsetDateTime(loan.borrowedAt()))
                .returnedAt(toOffsetDateTime(loan.returnedAt()));
    }

    private static OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }
}
