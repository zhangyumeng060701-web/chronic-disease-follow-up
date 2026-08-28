/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.security;

import com.example.followup.constant.DomainConstants;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * CurrentUser 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
public class CurrentUser {
    private Long userId;
    private String username;
    private String role;
    private Long patientId;

    public CurrentUser(Long userId, String username, String role) {
        this(userId, username, role, null);
    }

/**
 * 执行 isAdmin 操作。
 */
    public boolean isAdmin() {
        return DomainConstants.ROLE_ADMIN.equals(role);
    }

    public boolean isPatient() {
        return DomainConstants.ROLE_PATIENT.equals(role);
    }
}
