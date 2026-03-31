package com.usermanagement.service.impl;

import com.usermanagement.dto.request.UpdateUserRequest;
import com.usermanagement.dto.response.UserResponse;
import com.usermanagement.entity.User;
import com.usermanagement.exception.EmailAlreadyExistsException;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Default implementation of {@link UserService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with id={}", id);
        User user = findUserOrThrow(id);
        return mapToResponse(user);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id={}", id);
        User user = findUserOrThrow(id);

        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }

        if (StringUtils.hasText(request.getEmail())
                && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException(request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);
        log.info("User id={} updated successfully", id);
        return mapToResponse(saved);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id={}", id);
        User user = findUserOrThrow(id);
        userRepository.delete(user);
        log.info("User id={} deleted successfully", id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

