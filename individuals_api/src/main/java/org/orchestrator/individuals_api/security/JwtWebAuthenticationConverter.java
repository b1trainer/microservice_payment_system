package org.orchestrator.individuals_api.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import reactor.core.publisher.Mono;

import java.util.Collection;

public class JwtWebAuthenticationConverter {

    public static Converter<Jwt, Mono<AbstractAuthenticationToken>> getConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("roles");

        return jwt -> {
            Collection<GrantedAuthority> authorities = authoritiesConverter.convert(jwt);
            String username = jwt.getClaimAsString("preferred_username");
            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(
                    jwt, authorities, username);

            return Mono.just(authenticationToken);
        };
    }
}
