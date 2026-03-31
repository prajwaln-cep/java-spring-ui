package com.usermanagement.service;

import com.usermanagement.dto.request.UpdateUserRequest;
import com.usermanagement.dto.response.UserResponse;

import java.util.List;

/**
 * Contract for all user management operations.
 */
public interface UserService {

    /**
     * Retrieves all users.
     */
    List<UserResponse> getAllUsers();

    /**
     * Retrieves a single user by primary key.
     *
     * @throws com.usermanagement.exception.ResourceNotFoundException if not found
     */
    UserResponse getUserById(Long id);

    /**
     * Updates an existing user's mutable fields.
     *
     * @throws com.usermanagement.exception.ResourceNotFoundException  if not found
     * @throws com.usermanagement.exception.EmailAlreadyExistsException if new email is taken
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * Permanently removes a user.
     *
     * @throws com.usermanagement.exception.ResourceNotFoundException if not found
     */
    void deleteUser(Long id);
}

