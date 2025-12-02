package org.orchestrator.individuals_api.client;

import org.orchestrator.individuals_api.config.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.openapi.individuals.dto.TokenResponse;
import org.openapi.individuals.dto.UserInfoResponse;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakClient {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakClient.class);

    private final WebClient webClient;
    private final SecurityConfig securityConfig;

    public KeycloakClient(WebClient webClient, SecurityConfig securityConfig) {
        this.webClient = webClient;
        this.securityConfig = securityConfig;
    }

    public Mono<TokenResponse> getAdminAccessToken() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "client_credentials");
        params.add("client_id", securityConfig.getClientId());
        params.add("client_secret", securityConfig.getClientSecret());

        logger.info("Sending request to get access token");

        return webClient.post()
                .uri(securityConfig.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    public Mono<TokenResponse> createUser(String adminToken, String email, String password) {
        Map<String, Object> userRepresentation = new HashMap<>();

        userRepresentation.put("username", email);
        userRepresentation.put("email", email);
        userRepresentation.put("enabled", true);
        userRepresentation.put("emailVerified", false);
        userRepresentation.put("credentials", List.of(
                Map.of(
                        "type", "password",
                        "value", password,
                        "temporary", false
                )
        ));

        logger.info("Sending request to create user {}", email);

        return webClient.post()
                .uri(securityConfig.getTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer_" + adminToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(userRepresentation)
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    public Mono<TokenResponse> getAccessToken(String email, String password) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "password");
        params.add("username", email);
        params.add("password", password);
        params.add("client_id", securityConfig.getClientId());
        params.add("client_secret", securityConfig.getClientSecret());

        return webClient.post()
                .uri(securityConfig.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    public Mono<TokenResponse> refreshToken(String refreshToken) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);
        params.add("client_id", securityConfig.getClientId());
        params.add("client_secret", securityConfig.getClientSecret());

        logger.info("Sending request to refresh token");

        return webClient.post()
                .uri(securityConfig.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    public Mono<UserInfoResponse> getUserInfo(String accessToken) {
        logger.info("Sending request to get user info");
        return webClient.get()
                .uri(securityConfig.getUserInfoUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer_" + accessToken)
                .retrieve()
                .bodyToMono(UserInfoResponse.class);
    }
}
