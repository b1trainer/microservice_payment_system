package org.orchestrator.individuals_api.service.impl;

import org.openapi.individuals.dto.TokenRefreshRequest;
import org.openapi.individuals.dto.TokenResponse;
import org.openapi.individuals.dto.UserInfoResponse;
import org.openapi.individuals.dto.UserLoginRequest;
import org.openapi.individuals.dto.UserRegistrationRequest;
import org.orchestrator.individuals_api.client.KeycloakClient;
import org.orchestrator.individuals_api.exception.KeycloakAuthException;
import org.orchestrator.individuals_api.exception.UserInfoException;
import org.orchestrator.individuals_api.service.TokenService;
import org.orchestrator.individuals_api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final TokenService tokenService;
    private final KeycloakClient keycloakClient;

    public UserServiceImpl(TokenService tokenService, KeycloakClient keycloakClient) {
        this.tokenService = tokenService;
        this.keycloakClient = keycloakClient;
    }

    @Override
    public Mono<TokenResponse> signIn(UserRegistrationRequest signInRequest) {
        String userEmail = signInRequest.getEmail();

        logger.info("Starting registration for user: {}", userEmail);

        return keycloakClient.getAdminAccessToken()
                .onErrorMap(Exception.class, err -> new KeycloakAuthException("Failed to get admin access token: " + err))
                .flatMap(adminResponse ->
                        keycloakClient.createUser(adminResponse.getAccessToken(), userEmail, signInRequest.getPassword())
                                .doOnSuccess(res -> logger.info("User {} successfully created", userEmail))
                                .then(tokenService.getAccessToken(userEmail, signInRequest.getPassword()))
                                .doOnSuccess(res -> logger.info("User {} successfully sign in", userEmail))
                                .doOnError(error -> logger.error("User sign in is failed", error))
                );
    }

    @Override
    public Mono<TokenResponse> logIn(UserLoginRequest loginRequest) {
        return tokenService.getAccessToken(loginRequest.getEmail(), loginRequest.getPassword())
                .doOnSuccess(res -> logger.info("User {} successfully log in", loginRequest.getEmail()))
                .doOnError(error -> logger.error("User log in is failed", error));
    }

    @Override
    public Mono<TokenResponse> refreshToken(TokenRefreshRequest refreshRequest) {
        return tokenService.refreshToken(refreshRequest.getRefreshToken())
                .doOnSuccess(res -> logger.info("Token refresh is successful"))
                .doOnError(error -> logger.error("Token refresh is failed", error));
    }

    @Override
    public Mono<UserInfoResponse> getInfo(String accessToken) {
        return keycloakClient.getUserInfo(accessToken)
                .doOnSuccess(res -> logger.info("User info fetched successfully"))
                .doOnError(error -> logger.error("Failed to fetch user info", error))
                .onErrorMap(Exception.class, err -> new UserInfoException("Failed to fetch user information: " + err.getMessage()));
    }
}
