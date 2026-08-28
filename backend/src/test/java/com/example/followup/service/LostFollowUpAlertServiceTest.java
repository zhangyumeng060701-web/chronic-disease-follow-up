/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUp;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.service.impl.LostFollowUpAlertServiceImpl;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LostFollowUpAlertServiceTest {
    @Mock
    private FollowUpMapper followUpMapper;
    @Mock
    private AlertMapper alertMapper;
    @InjectMocks
    private LostFollowUpAlertServiceImpl lostFollowUpAlertService;

    @BeforeAll
    static void initTableMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), FollowUp.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), Alert.class);
    }

    @Test
    @DisplayName("没有逾期患者时不生成任何预警")
    void noOverduePatientsDoesNotGenerateAlerts() {
        when(followUpMapper.findOverduePatientIds()).thenReturn(Collections.emptyList());

        lostFollowUpAlertService.generateLostFollowUpAlerts();

        verify(followUpMapper, never()).selectList(any());
        verify(alertMapper, never()).selectList(any());
        verify(alertMapper, never()).batchInsert(anyList());
    }

    @Test
    @DisplayName("超过下次随访日期 7 天生成黄色失访预警")
    void sevenDaysOverdueGeneratesYellowAlert() {
        FollowUp followUp = followUp(1L, 1L, LocalDate.now().minusDays(7));
        when(followUpMapper.findOverduePatientIds()).thenReturn(List.of(1L));
        when(followUpMapper.selectList(any())).thenReturn(List.of(followUp));
        when(alertMapper.selectList(any())).thenReturn(Collections.emptyList());

        lostFollowUpAlertService.generateLostFollowUpAlerts();

        ArgumentCaptor<List<Alert>> captor = ArgumentCaptor.forClass(List.class);
        verify(alertMapper).batchInsert(captor.capture());
        Alert alert = captor.getValue().get(0);
        assertEquals(1L, alert.getPatientId());
        assertEquals("LOST_FOLLOW_UP", alert.getAlertType());
        assertEquals("YELLOW", alert.getAlertLevel());
        assertEquals(0, alert.getIsResolved());
    }

    @Test
    @DisplayName("超过下次随访日期 30 天生成红色失访预警")
    void thirtyDaysOverdueGeneratesRedAlert() {
        FollowUp followUp = followUp(1L, 1L, LocalDate.now().minusDays(30));
        when(followUpMapper.findOverduePatientIds()).thenReturn(List.of(1L));
        when(followUpMapper.selectList(any())).thenReturn(List.of(followUp));
        when(alertMapper.selectList(any())).thenReturn(Collections.emptyList());

        lostFollowUpAlertService.generateLostFollowUpAlerts();

        ArgumentCaptor<List<Alert>> captor = ArgumentCaptor.forClass(List.class);
        verify(alertMapper).batchInsert(captor.capture());
        Alert alert = captor.getValue().get(0);
        assertEquals(1L, alert.getPatientId());
        assertEquals("RED", alert.getAlertLevel());
    }

    @Test
    @DisplayName("未满 7 天不生成失访预警")
    void sixDaysOverdueDoesNotGenerateAlert() {
        FollowUp followUp = followUp(1L, 1L, LocalDate.now().minusDays(6));
        when(followUpMapper.findOverduePatientIds()).thenReturn(List.of(1L));
        when(followUpMapper.selectList(any())).thenReturn(List.of(followUp));
        when(alertMapper.selectList(any())).thenReturn(Collections.emptyList());

        lostFollowUpAlertService.generateLostFollowUpAlerts();

        verify(alertMapper, never()).batchInsert(anyList());
        verify(alertMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("已有黄色未处理预警时超过 30 天升级为红色")
    void existingYellowAlertUpgradedToRed() {
        FollowUp followUp = followUp(1L, 1L, LocalDate.now().minusDays(31));
        Alert existing = lostFollowUpAlert(1L, "YELLOW");
        when(followUpMapper.findOverduePatientIds()).thenReturn(List.of(1L));
        when(followUpMapper.selectList(any())).thenReturn(List.of(followUp));
        when(alertMapper.selectList(any())).thenReturn(List.of(existing));

        lostFollowUpAlertService.generateLostFollowUpAlerts();

        assertEquals("RED", existing.getAlertLevel());
        verify(alertMapper).updateById(existing);
        verify(alertMapper, never()).batchInsert(anyList());
    }

    @Test
    @DisplayName("已有相同等级未处理预警时不重复生成")
    void sameLevelUnresolvedAlertNotDuplicated() {
        FollowUp followUp = followUp(1L, 1L, LocalDate.now().minusDays(10));
        Alert existing = lostFollowUpAlert(1L, "YELLOW");
        when(followUpMapper.findOverduePatientIds()).thenReturn(List.of(1L));
        when(followUpMapper.selectList(any())).thenReturn(List.of(followUp));
        when(alertMapper.selectList(any())).thenReturn(List.of(existing));

        lostFollowUpAlertService.generateLostFollowUpAlerts();

        verify(alertMapper, never()).batchInsert(anyList());
        verify(alertMapper, never()).updateById(any());
    }

    private FollowUp followUp(Long id, Long patientId, LocalDate nextFollowUpDate) {
        FollowUp followUp = new FollowUp();
        followUp.setId(id);
        followUp.setPatientId(patientId);
        followUp.setNextFollowUpDate(nextFollowUpDate);
        return followUp;
    }

    private Alert lostFollowUpAlert(Long patientId, String level) {
        Alert alert = new Alert();
        alert.setPatientId(patientId);
        alert.setAlertType("LOST_FOLLOW_UP");
        alert.setAlertLevel(level);
        alert.setIsResolved(0);
        return alert;
    }
}
