package org.orchestrator.individuals_api.controller;

import org.openapi.individuals.dto.TokenResponse;
import org.openapi.individuals.dto.UserInfoResponse;
import org.openapi.individuals.dto.UserLoginRequest;
import org.openapi.individuals.dto.UserRegistrationRequest;
import org.openapi.individuals.dto.TokenRefreshRequest;
import org.orchestrator.individuals_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public Mono<ResponseEntity<TokenResponse>> register(@RequestBody UserRegistrationRequest registrationRequest) {
        return userService.signIn(registrationRequest)
                .map(token -> ResponseEntity.status(HttpStatus.CREATED).body(token));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponse>> login(@RequestBody UserLoginRequest loginRequest) {
        return userService.logIn(loginRequest)
                .map(token -> ResponseEntity.status(HttpStatus.OK).body(token));
    }

    @PostMapping("/refresh-token")
    public Mono<ResponseEntity<TokenResponse>> refreshToken(@RequestBody TokenRefreshRequest refreshRequest) {
        return userService.refreshToken(refreshRequest)
                .map(token -> ResponseEntity.status(HttpStatus.OK).body(token));
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<UserInfoResponse>> getUserInfo(@AuthenticationPrincipal JwtAuthenticationToken authentication) {
        return userService.getInfo(authentication)
                .map(user -> ResponseEntity.status(HttpStatus.OK).body(user));
    }
}
