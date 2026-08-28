/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.followup.exception.BusinessException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * SecurityUtilsTest 测试。
 *
 * @since 2026-08-28
 */
class SecurityUtilsTest {
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("未登录时抛出 401")
    void unauthenticatedThrows401() {
        BusinessException exception = assertThrows(BusinessException.class, SecurityUtils::currentUser);
        assertEquals(401, exception.getHttpStatus());
    }

    @Test
    @DisplayName("管理员上下文可正常读取")
    void adminContextIsReadable() {
        authenticate(new CurrentUser(1L, "admin", "ADMIN"), "ROLE_ADMIN");

        assertEquals(1L, SecurityUtils.currentUser().getUserId());
        assertTrue(SecurityUtils.isAdmin());
    }

    @Test
    @DisplayName("医生上下文可正常读取")
    void doctorContextIsReadable() {
        authenticate(new CurrentUser(7L, "doctor", "DOCTOR"), "ROLE_DOCTOR");

        assertEquals(7L, SecurityUtils.currentUser().getUserId());
    }

    private void authenticate(CurrentUser user, String authority) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority(authority)))
        );
    }
}
