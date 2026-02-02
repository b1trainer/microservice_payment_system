package org.orchestrator.individuals_api.service.impl;

import org.openapi.individuals.dto.TokenResponse;
import org.orchestrator.individuals_api.config.KeycloakConfig;
import org.orchestrator.individuals_api.config.SecurityConfig;
import org.orchestrator.individuals_api.exception.TokenRefreshException;
import org.orchestrator.individuals_api.service.TokenService;
import org.openapi.individuals.api.AuthenticationApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final AuthenticationApi authenticationApi;
    private final SecurityConfig securityConfig;
    private final KeycloakConfig keycloakConfig;

    private final AtomicReference<Instant> adminTokenExpiryTime = new AtomicReference<>();
    private final AtomicReference<TokenResponse> cachedAdminTokenResponse = new AtomicReference<>();

    public TokenServiceImpl(AuthenticationApi authenticationApi, SecurityConfig securityConfig, KeycloakConfig keycloakConfig) {
        this.authenticationApi = authenticationApi;
        this.securityConfig = securityConfig;
        this.keycloakConfig = keycloakConfig;
    }

    @Override
    public Mono<TokenResponse> refreshToken(String refreshToken) {
        return authenticationApi.getToken(
                        keycloakConfig.getRealm(),
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
                        keycloakConfig.getRealm(),
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

    @Override
    public Mono<TokenResponse> getAdminToken() {
        return getNewOrCachedAdminToken()
                .doOnSuccess(token -> LOGGER.debug("Admin token received successfully"))
                .doOnError(error -> LOGGER.error("Admin token receive failed", error))
                .onErrorMap(Exception.class,
                        error -> new AuthenticationException("Failed to get admin access token: " + error.getMessage()));
    }

    private Mono<TokenResponse> getNewOrCachedAdminToken() {
        if (cachedAdminTokenResponse.get() != null && Instant.now().isBefore(adminTokenExpiryTime.get())) {
            return Mono.just(cachedAdminTokenResponse.get());
        }

        return authenticationApi.getToken(
                        keycloakConfig.getRealm(),
                        "client_credentials",
                        securityConfig.getClientId(),
                        securityConfig.getClientSecret(),
                        null,
                        null,
                        null)
                .doOnSuccess(this::setCachedAdminToken);
    }

    private void setCachedAdminToken(TokenResponse token) {
        Instant expiryTime = Instant.now()
                .plusSeconds(token.getExpiresIn())
                .minusSeconds(keycloakConfig.getExpiryThresholdSec());

        adminTokenExpiryTime.set(expiryTime);
        cachedAdminTokenResponse.set(token);
        LOGGER.debug("Admin token successfully cached. Expiry time is {}", expiryTime);
    }
}
