package org.orchestrator.individuals_api.exception;

public class KeycloakAuthException extends RuntimeException {

    public KeycloakAuthException() {
        super("Keycloak authorization exception");
    }

    public KeycloakAuthException(String message) {
        super(message);
    }

    public KeycloakAuthException(String message, Throwable cause) {
        super(message, cause);
    }

}
