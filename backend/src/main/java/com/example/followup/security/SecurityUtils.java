/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.security;

import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {
    }

/**
 * 执行 currentUser 操作。
 */
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

/**
 * 执行 isPatient 操作。
 */
    public static boolean isPatient() {
        return currentUser().isPatient();
    }

    public static Long patientId() {
        CurrentUser currentUser = currentUser();
        if (!currentUser.isPatient() || currentUser.getPatientId() == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return currentUser.getPatientId();
    }
}
