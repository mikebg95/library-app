package dev.michaelgoldman.libraryapp.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import dev.michaelgoldman.libraryapp.application.port.in.LoanUseCases;
import dev.michaelgoldman.libraryapp.domain.model.BookId;
import dev.michaelgoldman.libraryapp.domain.model.Loan;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    private static final MemberId MEMBER = new MemberId(1);
    private static final BookId BOOK = new BookId(1);
    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Instant RETURNED_AT = Instant.parse("2026-10-26T10:00:00Z");

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private LoanUseCases loanUseCases;

    @Test
    void borrowBook_whenRequestIsValid_return201WithLoan() {
        Loan loan = Loan.start(MEMBER, BOOK, NOW);
        given(loanUseCases.borrow(MEMBER, BOOK)).willReturn(loan);

        assertThat(mvc.post()
                        .uri("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId": 1, "bookId": 1}
                                """))
                .hasStatus(HttpStatus.CREATED)
                .hasHeader(
                        "Location", "http://localhost/api/v1/loans/" + loan.id().value())
                .bodyJson()
                .isLenientlyEqualTo("""
                        {
                          "id": "%s",
                          "memberId": 1,
                          "bookId": 1,
                          "borrowedAt": "2026-10-19T10:00:00Z"
                        }
                        """.formatted(loan.id().value()));
    }

    @Test
    void returnBook_whenLoanExists_return200WithLoan() {
        Loan loan = Loan.start(MEMBER, BOOK, NOW);
        loan.returnBook(RETURNED_AT);
        given(loanUseCases.returnBook(loan.id())).willReturn(loan);

        assertThat(mvc.post().uri("/api/v1/loans/{loanId}/return", loan.id().value()))
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .isLenientlyEqualTo("""
                        {
                          "id": "%s",
                          "memberId": 1,
                          "bookId": 1,
                          "borrowedAt": "2026-10-19T10:00:00Z",
                          "returnedAt": "2026-10-26T10:00:00Z"
                        }
                        """.formatted(loan.id().value()));
    }

    @Test
    void getActiveLoans_whenMemberHasLoans_return200WithLoans() {
        Loan first = Loan.start(MEMBER, BOOK, NOW);
        Loan second = Loan.start(MEMBER, new BookId(2), NOW);
        given(loanUseCases.activeLoansOf(MEMBER)).willReturn(List.of(first, second));

        assertThat(mvc.get().uri("/api/v1/members/{memberId}/loans", MEMBER.value()))
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .isLenientlyEqualTo(
                        """
                        [
                          {"id": "%s", "memberId": 1, "bookId": 1, "borrowedAt": "2026-10-19T10:00:00Z"},
                          {"id": "%s", "memberId": 1, "bookId": 2, "borrowedAt": "2026-10-19T10:00:00Z"}
                        ]
                        """.formatted(first.id().value(), second.id().value()));
    }
}
