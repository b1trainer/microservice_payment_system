package org.orchestrator.individuals_api.service.impl;

import org.openapi.individuals.dto.TokenResponse;
import org.orchestrator.individuals_api.config.SecurityConfig;
import org.orchestrator.individuals_api.exception.TokenRefreshException;
import org.orchestrator.individuals_api.service.TokenService;
import org.openapi.individuals.api.AuthenticationApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;

@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final AuthenticationApi authenticationApi;
    private final SecurityConfig securityConfig;

    public TokenServiceImpl(AuthenticationApi authenticationApi, SecurityConfig securityConfig) {
        this.authenticationApi = authenticationApi;
        this.securityConfig = securityConfig;
    }

    @Override
    public Mono<TokenResponse> refreshToken(String refreshToken) {
        return authenticationApi.getToken(
                        securityConfig.getRealm(),
                        "refresh_token",
                        securityConfig.getClientId(),
                        securityConfig.getClientSecret(),
                        null,
                        null,
                        refreshToken
                )
                .doOnSuccess(token -> LOGGER.debug("Token refreshed successfully"))
                .doOnError(error -> LOGGER.error("Token refresh failed", error))
                .onErrorMap(Exception.class,
                        error -> new TokenRefreshException("Failed to refresh token: " + error.getMessage()));
    }

    @Override
    public Mono<TokenResponse> getAccessToken(String email, String password) {
        return authenticationApi.getToken(
                        securityConfig.getRealm(),
                        "password",
                        securityConfig.getClientId(),
                        securityConfig.getClientSecret(),
                        email,
                        password,
                        null
                )
                .doOnSuccess(token -> LOGGER.debug("Token for user {} received successfully", email))
                .doOnError(error -> LOGGER.error("Token receive failed", error))
                .onErrorMap(Exception.class,
                        error -> new AuthenticationException("Failed get access token: " + error.getMessage()));
    }
}
