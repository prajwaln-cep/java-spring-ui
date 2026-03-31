package com.usermanagement.service;

import com.usermanagement.dto.request.LoginRequest;
import com.usermanagement.dto.request.RegisterRequest;
import com.usermanagement.dto.response.AuthResponse;

/**
 * Contract for authentication operations (register / login).
 */
public interface AuthService {

    /**
     * Registers a new user and returns a JWT token pair.
     *
     * @throws com.usermanagement.exception.EmailAlreadyExistsException if email is taken
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user with email + password and returns a JWT token pair.
     *
     * @throws org.springframework.security.authentication.BadCredentialsException on failure
     */
    AuthResponse login(LoginRequest request);
}

