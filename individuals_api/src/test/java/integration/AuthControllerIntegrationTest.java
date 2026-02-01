package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.openapi.individuals.dto.UserRegistrationRequest;

@SpringBootTest
public class AuthControllerIntegrationTest {

    @Autowired
    private ApplicationContext context;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToApplicationContext(this.context)
                .configureClient()
                .build();
    }

    @Test
    void shouldRegisterUser() {
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest()
                .email("email@email.com")
                .password("password");

        webTestClient
                .post()
                .uri("/rest/v1/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationRequest)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldLogInUser() {
    }

    @Test
    void shouldRefreshToken() {
    }

    @Test
    void shouldReturnUserInfo() {
    }
}
