package org.orchestrator.individuals_api.service.impl;

import org.openapi.individuals.dto.TokenResponse;
import org.orchestrator.individuals_api.client.KeycloakClient;
import org.orchestrator.individuals_api.exception.TokenRefreshException;
import org.orchestrator.individuals_api.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;

@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final KeycloakClient keycloakClient;

    public TokenServiceImpl(KeycloakClient keycloakClient) {
        this.keycloakClient = keycloakClient;
    }

    @Override
    public Mono<TokenResponse> refreshToken(String refreshToken) {
        return keycloakClient.refreshToken(refreshToken)
                .doOnSuccess(token -> logger.debug("Token refreshed successfully"))
                .doOnError(error -> logger.error("Token refresh failed", error))
                .onErrorMap(Exception.class,
                        error -> new TokenRefreshException("Failed to refresh token: " + error.getMessage()));
    }

    @Override
    public Mono<TokenResponse> getAccessToken(String email, String password) {
        return keycloakClient.getAccessToken(email, password)
                .doOnSuccess(token -> logger.debug("Token for user {} received successfully", email))
                .doOnError(error -> logger.error("Token receive failed", error))
                .onErrorMap(Exception.class,
                        error -> new AuthenticationException("Failed get access token: " + error.getMessage()));
    }
}
