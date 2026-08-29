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
    /**
     * 查询listUsers。
     *
     * @param query 参数说明
     * @return 返回值
     */
    PageResponse<UserVO> listUsers(UserQuery query);
    /**
     * 新增createUser。
     *
     * @param request 参数说明
     */
    void createUser(CreateUserRequest request);
    /**
     * 更新updateUser。
     *
     * @param id 参数说明
     * @param request 参数说明
     */
    void updateUser(Long id, UpdateUserRequest request);
    /**
     * 执行toggleUserStatus操作。
     *
     * @param id 参数说明
     */
    void toggleUserStatus(Long id);
}
