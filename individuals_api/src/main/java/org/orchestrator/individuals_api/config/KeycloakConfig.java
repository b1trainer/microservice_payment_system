package org.orchestrator.individuals_api.config;

import org.openapi.individuals.invoker.ApiClient;
import org.openapi.individuals.api.AuthenticationApi;
import org.openapi.individuals.api.UsersApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.config.realm}")
    private String realm;

    @Value("${keycloak.config.expiryThresholdSec}")
    private Long expiryThresholdSec;

    @Bean
    public ApiClient keycloakApiClient(WebClient webClient) {
        return new ApiClient(webClient);
    }

    @Bean
    public AuthenticationApi authenticationApi(ApiClient keycloakApiClient) {
        return new AuthenticationApi(keycloakApiClient);
    }

    @Bean
    public UsersApi usersApi(ApiClient apiClient) {
        return new UsersApi(apiClient);
    }

    public String getRealm() {
        return realm;
    }

    public Long getExpiryThresholdSec() {
        return expiryThresholdSec;
    }
}
