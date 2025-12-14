package org.orchestrator.individuals_api.config;

import org.orchestrator.individuals_api.security.JwtWebAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.config.realm}")
    private String realm;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .matchers(publicPaths()).permitAll()
                        .matchers(privatePaths()).authenticated()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(JwtWebAuthenticationConverter.getToken()))
                )
                .build();
    }

    private ServerWebExchangeMatcher[] publicPaths() {
        return new ServerWebExchangeMatcher[]{
                pathMatcher("/actuator/**"),
                pathMatcher("/v1/auth/login"),
                pathMatcher("/v1/auth/registration"),
                pathMatcher("/v1/auth/refresh-token"),
        };
    }

    private ServerWebExchangeMatcher[] privatePaths() {
        return new ServerWebExchangeMatcher[]{
                pathMatcher("/v1/auth/me")
        };
    }

    private PathPatternParserServerWebExchangeMatcher pathMatcher(String pattern) {
        return new PathPatternParserServerWebExchangeMatcher(pattern);
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRealm() {
        return realm;
    }
}
