package com.usermanagement.controller;

import com.usermanagement.dto.request.LoginRequest;
import com.usermanagement.dto.request.RegisterRequest;
import com.usermanagement.dto.response.ApiResponse;
import com.usermanagement.dto.response.AuthResponse;
import com.usermanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 *
 * <ul>
 *   <li>POST /auth/register — create new account</li>
 *   <li>POST /auth/login    — obtain JWT tokens</li>
 * </ul>
 *
 * Base path: /api/v1/auth  (prefix set in application.yml)
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user account.
     *
     * @param request name, email, password
     * @return 201 Created with JWT tokens and user info
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("POST /auth/register  email={}", request.getEmail());
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authResponse));
    }

    /**
     * Authenticate an existing user.
     *
     * @param request email + password
     * @return 200 OK with JWT tokens and user info
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("POST /auth/login  email={}", request.getEmail());
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }
}

