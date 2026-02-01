package org.orchestrator.individuals_api.integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.openapi.individuals.dto.UserRegistrationRequest;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.openapi.individuals.dto.CredentialRepresentation;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthControllerIntegrationTest {

    @Container
    private static final KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile("realm-config.json");

    @DynamicPropertySource
    static void configureKeycloakProperties(DynamicPropertyRegistry registry) {
        String issuerUri = keycloak.getAuthServerUrl() + "/realms/orchestrator";
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> issuerUri);
        registry.add("spring.security.oauth2.client.registration.keycloak.client-secret", () -> "kqXKpfxQyaYkl9iqAc6AMeowishFLkNz");
        registry.add("keycloak.config.realm", () -> "orchestrator");
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldRegisterUser() {
        List<CredentialRepresentation> credentialRepresentation = List.of(
                new CredentialRepresentation()
                        .type(CredentialRepresentation.TypeEnum.PASSWORD)
                        .value("password")
                        .temporary(false)
        );

        UserRegistrationRequest registrationRequest = new UserRegistrationRequest()
                .username("email@email.com")
                .email("email@email.com")
                .enabled(true)
                .emailVerified(false)
                .credentials(credentialRepresentation);

        webTestClient
                .post()
                .uri("/v1/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationRequest)
                .exchange()
                .expectStatus().isOk();
    }

//    @Test
//    void shouldLogInUser() {
//    }
//
//    @Test
//    void shouldRefreshToken() {
//    }
//
//    @Test
//    void shouldReturnUserInfo() {
//    }
}
