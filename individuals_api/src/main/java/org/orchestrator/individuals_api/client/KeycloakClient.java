package org.orchestrator.individuals_api.client;

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

    @Value("${spring.security.oauth2.client.registration.provider.keycloak.token-uri}")
    private String TOKEN_URL;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String CLIENT_SECRET;

    public String getAccessToken(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FROM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiVAlueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, Map.class);

        return response.getBody().get("access_token").toString();
    }
}
