package com.creepvocab.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * Get the current logged-in user ID
     */
    public static Long getUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getPrincipal() == null) {
                throw new RuntimeException("User not logged in");
            }
            Object principal = authentication.getPrincipal();
            return Long.valueOf(principal.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user ID", e);
        }
    }
}
