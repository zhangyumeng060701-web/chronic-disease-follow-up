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
    PageResponse<OperationLog> listLogs(LogQuery query);

    default void log(Long userId, String username, String operation, String targetType, Long targetId) {
        /**
         * 执行log操作。
         *
         * @param userId 参数说明
         * @param username 参数说明
         * @param operation 参数说明
         * @param targetType 参数说明
         * @param targetId 参数说明
         * @param null 参数说明
         */
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
