package org.orchestrator.individuals_api.service;

import reactor.core.publisher.Mono;
import org.openapi.individuals.dto.TokenResponse;

public interface TokenService {

    Mono<TokenResponse> refreshToken(String refreshToken);

    Mono<TokenResponse> getAccessToken(String email, String password);

    Mono<TokenResponse> getAdminToken();

}
