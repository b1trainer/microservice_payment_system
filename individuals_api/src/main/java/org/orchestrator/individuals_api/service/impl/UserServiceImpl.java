package org.orchestrator.individuals_api.service.impl;

import org.openapi.individuals.dto.TokenRefreshRequest;
import org.openapi.individuals.dto.TokenResponse;
import org.openapi.individuals.dto.UserInfoResponse;
import org.openapi.individuals.dto.UserLoginRequest;
import org.openapi.individuals.dto.UserRegistrationRequest;
import org.orchestrator.individuals_api.config.SecurityConfig;
import org.orchestrator.individuals_api.exception.UserInfoException;
import org.orchestrator.individuals_api.service.TokenService;
import org.orchestrator.individuals_api.service.UserService;
import org.openapi.individuals.api.UsersApi;
import org.openapi.individuals.invoker.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UsersApi usersApi;
    private final ApiClient apiClient;
    private final TokenService tokenService;
    private final SecurityConfig securityConfig;

    public UserServiceImpl(TokenService tokenService, SecurityConfig securityConfig,
                           UsersApi usersApi, ApiClient apiClient) {
        this.usersApi = usersApi;
        this.apiClient = apiClient;
        this.tokenService = tokenService;
        this.securityConfig = securityConfig;
    }

    @Override
    public Mono<TokenResponse> signIn(UserRegistrationRequest signInRequest) {
        String userEmail = signInRequest.getEmail();

        LOGGER.info("Starting registration for user: {}", userEmail);

        return tokenService.getAdminToken()
                .flatMap(adminResponse ->
                        {
                            apiClient.setBasePath("");
                            apiClient.setBearerToken(adminResponse.getAccessToken());
                            return usersApi.createUser(securityConfig.getRealm(), signInRequest)
                                    .doOnSuccess(res -> LOGGER.info("User {} successfully created", userEmail))
                                    .then(tokenService.getAccessToken(userEmail, signInRequest.getCredentials().getFirst().getValue()))
                                    .doOnSuccess(res -> LOGGER.info("User {} successfully sign in", userEmail))
                                    .doOnError(error -> LOGGER.error("User sign in is failed", error));
                        }
                );
    }

    @Override
    public Mono<TokenResponse> logIn(UserLoginRequest loginRequest) {
        return tokenService.getAccessToken(loginRequest.getEmail(), loginRequest.getPassword())
                .doOnSuccess(res -> LOGGER.info("User {} successfully log in", loginRequest.getEmail()))
                .doOnError(error -> LOGGER.error("User log in is failed", error));
    }

    @Override
    public Mono<TokenResponse> refreshToken(TokenRefreshRequest refreshRequest) {
        return tokenService.refreshToken(refreshRequest.getRefreshToken())
                .doOnSuccess(res -> LOGGER.info("Token refresh is successful"))
                .doOnError(error -> LOGGER.error("Token refresh is failed", error));
    }

    @Override
    public Mono<UserInfoResponse> getInfo(String userId) {
        return usersApi.getUserInfoById(securityConfig.getRealm(), userId)
                .doOnSuccess(res -> LOGGER.info("User info fetched successfully"))
                .doOnError(error -> LOGGER.error("Failed to fetch user info", error))
                .onErrorMap(Exception.class, err -> new UserInfoException("Failed to fetch user information: " + err.getMessage()));
    }
}
