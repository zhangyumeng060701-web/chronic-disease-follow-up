/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.followup.entity.Alert;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.impl.AlertServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * AlertServiceTest 测试。
 *
 * @since 2026-08-28
 */
@ExtendWith(MockitoExtension.class)
class AlertServiceTest {
    @Mock
    private AlertMapper alertMapper;
    @Mock
    private PatientMapper patientMapper;
    @InjectMocks
    private AlertServiceImpl alertService;

    @Test
    @DisplayName("不存在的预警返回 404")
    void resolveNotFound() {
        when(alertMapper.selectById(9L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> alertService.resolveAlert(9L));
        assertEquals(404, exception.getHttpStatus());
    }

    @Test
    @DisplayName("处理预警后更新为已解决")
    void resolveUpdatesAlert() {
        Alert alert = new Alert();
        alert.setId(9L);
        alert.setIsResolved(0);
        alert.setAlertStatus("PENDING");
        when(alertMapper.selectById(9L)).thenReturn(alert);

        alertService.resolveAlert(9L);

        assertEquals(1, alert.getIsResolved());
        assertEquals("RESOLVED", alert.getAlertStatus());
        verify(alertMapper).updateById(alert);
    }

    @Test
    @DisplayName("标记预警为已联系")
    void contactMarksAlert() {
        Alert alert = new Alert();
        alert.setId(3L);
        alert.setAlertStatus("PENDING");
        when(alertMapper.selectById(3L)).thenReturn(alert);

        alertService.contactAlert(3L);

        assertEquals("CONTACTED", alert.getAlertStatus());
        verify(alertMapper).updateById(alert);
    }

    @Test
    @DisplayName("预警转门诊记录原因")
    void referMarksAlert() {
        Alert alert = new Alert();
        alert.setId(4L);
        alert.setIsResolved(0);
        alert.setAlertStatus("CONTACTED");
        when(alertMapper.selectById(4L)).thenReturn(alert);

        alertService.referAlert(4L, "血压持续偏高，需上级医院评估");

        assertEquals(1, alert.getIsResolved());
        assertEquals("REFERRED", alert.getAlertStatus());
        assertEquals("血压持续偏高，需上级医院评估", alert.getReferralReason());
        verify(alertMapper).updateById(alert);
    }
}
