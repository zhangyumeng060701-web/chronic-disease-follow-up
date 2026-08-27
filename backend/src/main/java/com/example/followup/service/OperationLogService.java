package com.example.followup.service;

import com.example.followup.dto.request.LogQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.OperationLog;

public interface OperationLogService {
    PageResponse<OperationLog> listLogs(LogQuery query);

    default void log(Long userId, String username, String operation, String targetType, Long targetId) {
        log(userId, username, operation, targetType, targetId, null);
    }

    void log(Long userId, String username, String operation, String targetType, Long targetId, String detail);
}
