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

    /**
     * 执行CurrentUser操作。
     *
     * @param userId 参数说明
     * @param username 参数说明
     * @param role 参数说明
     */
    public CurrentUser(Long userId, String username, String role) {
        this(userId, username, role, null);
    }

    /**
     * 判断isAdmin。
     *
     * @return 返回值
     */
    public boolean isAdmin() {
        return DomainConstants.ROLE_ADMIN.equals(role);
    }

    /**
     * 判断isPatient。
     *
     * @return 返回值
     */
    public boolean isPatient() {
        return DomainConstants.ROLE_PATIENT.equals(role);
    }
}
