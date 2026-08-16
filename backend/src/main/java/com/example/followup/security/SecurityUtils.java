package com.example.followup.security;

import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static CurrentUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CurrentUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return (CurrentUser) authentication.getPrincipal();
    }

    public static boolean isAdmin() {
        return currentUser().isAdmin();
    }
}
