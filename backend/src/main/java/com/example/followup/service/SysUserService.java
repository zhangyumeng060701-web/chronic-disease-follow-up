/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.service;

import com.example.followup.dto.request.CreateUserRequest;
import com.example.followup.dto.request.UpdateUserRequest;
import com.example.followup.dto.request.UserQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.UserVO;

/**
 * SysUserService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface SysUserService {
    PageResponse<UserVO> listUsers(UserQuery query);
    void createUser(CreateUserRequest request);
    void updateUser(Long id, UpdateUserRequest request);
    void toggleUserStatus(Long id);
}
