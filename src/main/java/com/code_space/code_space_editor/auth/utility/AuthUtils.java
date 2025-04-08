package com.code_space.code_space_editor.auth.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.code_space.code_space_editor.auth.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("You are not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user.getId();
        } else {
            throw new RuntimeException("You are not authenticated.");
        }
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("You are not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user.getUsername();
        } else {
            throw new RuntimeException("You are not authenticated.");
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("You are not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user;
        } else {
            throw new RuntimeException("You are not authenticated.");
        }
    }
}
