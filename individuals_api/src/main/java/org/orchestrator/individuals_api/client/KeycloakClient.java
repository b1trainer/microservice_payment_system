package org.orchestrator.individuals_api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Добавить в keycloak clients Authorization или Service Account roles
 * Для сервисов нужно создавать отдельного клиента
 */
@Component
public class KeycloakClient {

    private final RestTemplate restTemplate;

    public KeycloakClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String TOKEN_URL;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;


    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, Map.class);
        return (String) response.getBody().get("access_token");
    }
}
