package dev.michaelgoldman.libraryapp.adapter.in.web;

import dev.michaelgoldman.libraryapp.domain.exception.BookAlreadyOnLoanException;
import dev.michaelgoldman.libraryapp.domain.exception.BookNotFoundException;
import dev.michaelgoldman.libraryapp.domain.exception.LoanAlreadyReturnedException;
import dev.michaelgoldman.libraryapp.domain.exception.LoanNotFoundException;
import dev.michaelgoldman.libraryapp.domain.exception.MaxLoansReachedException;
import dev.michaelgoldman.libraryapp.domain.exception.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({MemberNotFoundException.class, BookNotFoundException.class, LoanNotFoundException.class})
    ProblemDetail handleNotFound(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({
        BookAlreadyOnLoanException.class,
        MaxLoansReachedException.class,
        LoanAlreadyReturnedException.class
    })
    ProblemDetail handleConflict(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }
}
