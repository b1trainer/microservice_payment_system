package org.orchestrator.individuals_api.config;

import org.orchestrator.individuals_api.exception.UnauthorizedException;
import org.orchestrator.individuals_api.security.JwtWebAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUrl;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUrl;

    @Value("${spring.security.oauth2.client.provider.keycloak.user-info-uri}")
    private String userInfoUrl;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    private final String[] NO_AUTH_PATHS = {"/actuator/**", "/v1/auth/login", "/v1/auth/register", "/v1/auth/refresh-token"};

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(
                                                () -> {
                                                    throw new UnauthorizedException("Failed to authenticate user");
                                                }))
                                )
                                .authorizeExchange(auth -> auth
                                        .pathMatchers(NO_AUTH_PATHS).permitAll()
                                        .pathMatchers("/v1/auth/me").authenticated()
                                        .anyExchange().authenticated()
                                )
                                .oauth2ResourceServer(oauth2 -> oauth2
                                        .jwt(jwt -> jwt.jwtAuthenticationConverter(JwtWebAuthenticationConverter.getToken()))
                                )
                                .build();
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getUserInfoUrl() {
        return userInfoUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getIssuerUrl() {
        return issuerUrl;
    }
}
