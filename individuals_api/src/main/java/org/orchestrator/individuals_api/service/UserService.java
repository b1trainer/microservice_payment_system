package org.orchestrator.individuals_api.service;

import org.openapi.individuals.dto.TokenResponse;
import org.openapi.individuals.dto.UserInfoResponse;
import org.openapi.individuals.dto.UserRegistrationRequest;
import org.openapi.individuals.dto.UserLoginRequest;
import org.openapi.individuals.dto.TokenRefreshRequest;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<TokenResponse> signIn(UserRegistrationRequest signInRequest);

    Mono<TokenResponse> logIn(UserLoginRequest loginRequest);

    Mono<TokenResponse> refreshToken(TokenRefreshRequest refreshRequest);

    Mono<UserInfoResponse> getInfo(JwtAuthenticationToken token);
}
