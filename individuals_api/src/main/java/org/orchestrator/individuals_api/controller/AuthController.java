package org.orchestrator.individuals_api.controller;

import org.openapi.individuals.dto.TokenResponse;
import org.openapi.individuals.dto.UserInfoResponse;
import org.openapi.individuals.dto.UserLoginRequest;
import org.openapi.individuals.dto.UserRegistrationRequest;
import org.openapi.individuals.dto.TokenRefreshRequest;
import org.orchestrator.individuals_api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public Mono<ResponseEntity<TokenResponse>> register(@RequestBody UserRegistrationRequest registrationRequest) {
        return Mono.just(registrationRequest)
                .doOnNext(req ->
                        logger.info("Registration request from: {}", req.getEmail()))
                .flatMap(userService::signIn)
                .doOnSuccess(token ->
                        logger.info("Registration successful"))
                .doOnError(error ->
                        logger.error("Registration request failed: {}", error.getMessage())
                )
                .map(token -> ResponseEntity.status(HttpStatus.CREATED).body(token));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponse>> login(@RequestBody UserLoginRequest loginRequest) {
        return Mono.just(loginRequest)
                .doOnNext(req ->
                        logger.info("Login request from: {}", req.getEmail()))
                .flatMap(userService::logIn)
                .doOnSuccess(token ->
                        logger.info("Login successful"))
                .doOnError(error ->
                        logger.error("Login request failed: {}", error.getMessage())
                )
                .map(token -> ResponseEntity.status(HttpStatus.OK).body(token));
    }

    @PostMapping("/refresh-token")
    public Mono<ResponseEntity<TokenResponse>> refreshToken(@RequestBody TokenRefreshRequest refreshRequest) {
        return Mono.just(refreshRequest)
                .doOnNext(req ->
                        logger.info("Token refresh request"))
                .flatMap(userService::refreshToken)
                .doOnSuccess(token ->
                        logger.info("Token refreshed successfully"))
                .doOnError(error ->
                        logger.error("Token refresh request failed: {}", error.getMessage())
                )
                .map(token -> ResponseEntity.status(HttpStatus.OK).body(token));
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<UserInfoResponse>> getUserInfo() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context ->
                        ((JwtAuthenticationToken) context.getAuthentication())
                )
                .doOnNext(token ->
                        logger.info("User info get request"))
                .flatMap(userService::getInfo)
                .doOnSuccess(userInfo ->
                        logger.info("Successfully received info for user: {}", userInfo.getId()))
                .doOnError(error ->
                        logger.error("Get user info request failed: {}", error.getMessage())
                )
                .map(user -> ResponseEntity.status(HttpStatus.OK).body(user));
    }
}
