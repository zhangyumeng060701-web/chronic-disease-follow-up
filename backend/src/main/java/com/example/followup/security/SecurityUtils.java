/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.security;

import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityUtils 工具类。
 *
 * @since 2026-08-28
 */
public final class SecurityUtils {
    private SecurityUtils() {
    }

    /**
     * 执行currentUser操作。
     *
     * @return 返回值
     */
    public static CurrentUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CurrentUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return (CurrentUser) authentication.getPrincipal();
    }

    /**
     * 判断isAdmin。
     *
     * @return 返回值
     */
    public static boolean isAdmin() {
        return currentUser().isAdmin();
    }

    /**
     * 判断isPatient。
     *
     * @return 返回值
     */
    public static boolean isPatient() {
        return currentUser().isPatient();
    }

    /**
     * 执行patientId操作。
     *
     * @return 返回值
     */
    public static Long patientId() {
        CurrentUser currentUser = currentUser();
        if (!currentUser.isPatient() || currentUser.getPatientId() == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return currentUser.getPatientId();
    }
}
