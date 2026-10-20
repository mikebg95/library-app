package dev.michaelgoldman.libraryapp;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class LoanEndToEndTest {

    @Autowired
    private RestTestClient client;

    @Test
    void borrowAndReturnBook_updateActiveLoansOfMember() {
        URI loan = client.post()
                .uri("/api/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"memberId": 1, "bookId": 1}
                        """)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult()
                .getResponseHeaders()
                .getLocation();

        client.get()
                .uri("/api/v1/members/1/loans")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(1);

        client.post()
                .uri(loan + "/return")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.returnedAt")
                .exists();

        client.get()
                .uri("/api/v1/members/1/loans")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(0);
    }
}
