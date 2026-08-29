/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.LogQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.OperationLog;

/**
 * OperationLogService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface OperationLogService {
    /**
     * 查询操作日志。
     *
     * @param query 查询条件
     * @return 操作日志分页结果
     */
    PageResponse<OperationLog> listLogs(LogQuery query);

    /**
     * 记录操作日志。
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param operation 操作名称
     * @param targetType 目标类型
     * @param targetId 目标ID
     */
    default void log(Long userId, String username, String operation, String targetType, Long targetId) {
        log(userId, username, operation, targetType, targetId, null);
    }

    /**
     * 执行log操作。
     *
     * @param userId 参数说明
     * @param username 参数说明
     * @param operation 参数说明
     * @param targetType 参数说明
     * @param targetId 参数说明
     * @param detail 参数说明
     */
    void log(Long userId, String username, String operation, String targetType, Long targetId, String detail);
}
