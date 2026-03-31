package com.usermanagement.security;

import com.usermanagement.entity.User;
import com.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Spring Security expression helper bean — referenced in {@code @PreAuthorize} annotations
 * as {@code @userSecurity}.
 *
 * <p>Allows a regular USER to access only their own resource while ADMINs bypass the check.</p>
 */
@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    /**
     * Returns {@code true} if the authenticated principal is the same user as {@code userId}.
     *
     * @param authentication current Spring Security authentication
     * @param userId         the path variable id being accessed
     */
    public boolean isOwner(Authentication authentication, Long userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String email = authentication.getName();
        return userRepository.findById(userId)
                .map(User::getEmail)
                .map(userEmail -> userEmail.equalsIgnoreCase(email))
                .orElse(false);
    }
}

