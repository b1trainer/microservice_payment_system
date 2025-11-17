package org.orchestrator.individuals_api.config;

import org.orchestrator.individuals_api.security.JwtWebAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String[] NO_AUTH_PATHS = {"/actuator/**", "/v1/auth/login", "/v1/auth/register", "/v1/auth/refresh-token"};

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(NO_AUTH_PATHS).permitAll()
                        .pathMatchers("/v1/auth/me").authenticated()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(JwtWebAuthenticationConverter.getConverter()))
                )
                .build();
    }
}
