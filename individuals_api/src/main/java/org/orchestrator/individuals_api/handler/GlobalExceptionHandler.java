package org.orchestrator.individuals_api.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.openapi.individuals.dto.ErrorResponse;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleException(
            Exception ex, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
                    logger.error("Handle error: {}", ex.getMessage());

                    ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.setError(ex.getMessage());
                    errorResponse.setStatus(exchange.getResponse().getStatusCode().value());
                    return errorResponse;
                })
                .map(response -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}
