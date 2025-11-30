package org.orchestrator.individuals_api.handler;

import jakarta.validation.ValidationException;
import org.orchestrator.individuals_api.exception.UnauthorizedException;
import org.orchestrator.individuals_api.exception.UserAlreadyExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.openapi.individuals.dto.ErrorResponse;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleException(Exception ex, ServerWebExchange exchange) {
        return getErrorResponseMono(ex, exchange)
                .map(response -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(ValidationException ex, ServerWebExchange exchange) {
        return getErrorResponseMono(ex, exchange)
                .map(response -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnauthorizedException(UnauthorizedException ex, ServerWebExchange exchange) {
        return getErrorResponseMono(ex, exchange)
                .map(response -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserExistException(UserAlreadyExistException ex, ServerWebExchange exchange) {
        return getErrorResponseMono(ex, exchange)
                .map(response -> ResponseEntity.status(HttpStatus.CONFLICT).body(response));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotExistException(UsernameNotFoundException ex, ServerWebExchange exchange) {
        return getErrorResponseMono(ex, exchange)
                .map(response -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    private static Mono<ErrorResponse> getErrorResponseMono(Exception ex, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            logger.error("Handling error: {}", ex.getMessage());

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(ex.getMessage());
            errorResponse.setStatus(exchange.getResponse().getStatusCode().value());
            return errorResponse;
        });
    }

}
