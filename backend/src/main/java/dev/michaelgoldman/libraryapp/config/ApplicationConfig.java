package dev.michaelgoldman.libraryapp.config;

import dev.michaelgoldman.libraryapp.application.port.in.LoanUseCases;
import dev.michaelgoldman.libraryapp.application.port.out.BookRepository;
import dev.michaelgoldman.libraryapp.application.port.out.LoanRepository;
import dev.michaelgoldman.libraryapp.application.port.out.MemberRepository;
import dev.michaelgoldman.libraryapp.application.service.LoanService;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ApplicationConfig {

    @Bean
    LoanUseCases loanUseCases(MemberRepository members, BookRepository books, LoanRepository loans) {
        return new LoanService(members, books, loans, Clock.systemUTC());
    }
}
