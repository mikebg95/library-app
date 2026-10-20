package dev.michaelgoldman.libraryapp.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import dev.michaelgoldman.libraryapp.application.port.in.LoanUseCases;
import dev.michaelgoldman.libraryapp.domain.exception.BookAlreadyOnLoanException;
import dev.michaelgoldman.libraryapp.domain.exception.MemberNotFoundException;
import dev.michaelgoldman.libraryapp.domain.model.BookId;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(LoanController.class)
class ErrorHandlerTest {

    private static final MemberId MEMBER = new MemberId(1);
    private static final BookId BOOK = new BookId(1);

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private LoanUseCases loanUseCases;

    @Test
    void borrowBook_whenMemberIsUnknown_return404WithProblem() {
        given(loanUseCases.borrow(MEMBER, BOOK)).willThrow(new MemberNotFoundException(MEMBER));

        assertThat(borrow("""
                        {"memberId": 1, "bookId": 1}
                        """))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson()
                .isLenientlyEqualTo("""
                        {"status": 404, "detail": "Member 1 not found"}
                        """);
    }

    @Test
    void borrowBook_whenBookIsAlreadyOnLoan_return409WithProblem() {
        given(loanUseCases.borrow(MEMBER, BOOK)).willThrow(new BookAlreadyOnLoanException(BOOK));

        assertThat(borrow("""
                        {"memberId": 1, "bookId": 1}
                        """))
                .hasStatus(HttpStatus.CONFLICT)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson()
                .isLenientlyEqualTo("""
                        {"status": 409, "detail": "Book 1 is already on loan"}
                        """);
    }

    @Test
    void borrowBook_whenMemberIdIsMissing_return400WithProblem() {
        assertThat(borrow("""
                        {"bookId": 1}
                        """))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson()
                .isLenientlyEqualTo("""
                        {"status": 400}
                        """);
    }

    private MockMvcTester.MockMvcRequestBuilder borrow(String body) {
        return mvc.post()
                .uri("/api/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
