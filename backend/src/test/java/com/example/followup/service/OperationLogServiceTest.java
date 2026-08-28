/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.example.followup.entity.OperationLog;
import com.example.followup.mapper.OperationLogMapper;
import com.example.followup.service.impl.OperationLogServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OperationLogServiceTest 测试。
 *
 * @since 2026-08-28
 */
@ExtendWith(MockitoExtension.class)
class OperationLogServiceTest {
    @Mock
    private OperationLogMapper operationLogMapper;
    @InjectMocks
    private OperationLogServiceImpl operationLogService;

    @Test
    @DisplayName("记录操作日志时写入用户、操作和 IP")
    void logWritesAuditFields() {
        operationLogService.log(1L, "admin", "新增患者", "Patient", 5L);

        ArgumentCaptor<OperationLog> captor = ArgumentCaptor.forClass(OperationLog.class);
        verify(operationLogMapper).insert(captor.capture());
        OperationLog log = captor.getValue();
        assertEquals(1L, log.getUserId());
        assertEquals("admin", log.getUsername());
        assertEquals("新增患者", log.getOperation());
        assertEquals("Patient", log.getTargetType());
        assertEquals(5L, log.getTargetId());
        assertEquals("unknown", log.getIpAddress());
    }
}
