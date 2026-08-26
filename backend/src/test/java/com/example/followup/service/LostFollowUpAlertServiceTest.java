package com.example.followup.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.LostFollowUpAlertRecord;
import com.example.followup.mapper.LostFollowUpQueryMapper;
import com.example.followup.service.impl.LostFollowUpAlertServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LostFollowUpAlertServiceTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 8, 26);

    @Mock
    private LostFollowUpQueryMapper mapper;
    private LostFollowUpAlertServiceImpl service;

    @BeforeAll
    static void initMetadata() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "lost-alert-test"),
                LostFollowUpAlertRecord.class);
    }

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-26T00:00:00Z"), ZoneId.of("UTC"));
        service = new LostFollowUpAlertServiceImpl(mapper, clock);
    }

    @Test
    @DisplayName("扫描只查询至少逾期7天的最新随访")
    void scansFromSevenDayBoundary() {
        when(mapper.findLatestDueFollowUps(TODAY.minusDays(7))).thenReturn(Collections.emptyList());

        LostFollowUpScanResult result = service.scanAndGenerateAlerts();

        assertEquals(0, result.getScannedCount());
        verify(mapper).findLatestDueFollowUps(TODAY.minusDays(7));
    }

    @Test
    @DisplayName("恰好逾期7天生成黄色预警")
    void createsYellowAtSevenDays() {
        when(mapper.findLatestDueFollowUps(any())).thenReturn(List.of(followUp(7)));
        when(mapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(mapper.insert(any(LostFollowUpAlertRecord.class))).thenAnswer(invocation -> {
            LostFollowUpAlertRecord alert = invocation.getArgument(0);
            assertEquals("YELLOW", alert.getAlertLevel());
            assertEquals(TODAY.minusDays(7), alert.getSourceDueDate());
            return 1;
        });

        LostFollowUpScanResult result = service.scanAndGenerateAlerts();

        assertEquals(1, result.getYellowCreated());
        assertEquals(0, result.getRedCreated());
    }

    @Test
    @DisplayName("逾期29天仍为黄色预警")
    void keepsYellowAtTwentyNineDays() {
        when(mapper.findLatestDueFollowUps(any())).thenReturn(List.of(followUp(29)));
        when(mapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(mapper.insert(any(LostFollowUpAlertRecord.class))).thenAnswer(invocation -> {
            assertEquals("YELLOW", invocation.<LostFollowUpAlertRecord>getArgument(0).getAlertLevel());
            return 1;
        });

        assertEquals(1, service.scanAndGenerateAlerts().getYellowCreated());
    }

    @Test
    @DisplayName("恰好逾期30天关闭黄色并生成红色预警")
    void upgradesToRedAtThirtyDays() {
        LostFollowUpAlertRecord yellow = existingYellow();
        when(mapper.findLatestDueFollowUps(any())).thenReturn(List.of(followUp(30)));
        when(mapper.selectList(any(Wrapper.class))).thenReturn(List.of(yellow));
        when(mapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(mapper.insert(any(LostFollowUpAlertRecord.class))).thenAnswer(invocation -> {
            assertEquals("RED", invocation.<LostFollowUpAlertRecord>getArgument(0).getAlertLevel());
            return 1;
        });

        LostFollowUpScanResult result = service.scanAndGenerateAlerts();

        assertEquals(1, result.getYellowResolved());
        assertEquals(1, result.getRedCreated());
        assertEquals(1, yellow.getIsResolved());
        verify(mapper).updateById(yellow);
    }

    @Test
    @DisplayName("同周期同级别预警已存在时保持幂等")
    void skipsExistingCycleAlert() {
        when(mapper.findLatestDueFollowUps(any())).thenReturn(List.of(followUp(8)));
        when(mapper.selectCount(any(Wrapper.class))).thenReturn(1L);

        LostFollowUpScanResult result = service.scanAndGenerateAlerts();

        assertEquals(1, result.getSkippedCount());
        verify(mapper, never()).insert(any());
    }

    private FollowUp followUp(int overdueDays) {
        FollowUp followUp = new FollowUp();
        followUp.setPatientId(9L);
        followUp.setNextFollowUpDate(TODAY.minusDays(overdueDays));
        return followUp;
    }

    private LostFollowUpAlertRecord existingYellow() {
        LostFollowUpAlertRecord alert = new LostFollowUpAlertRecord();
        alert.setId(1L);
        alert.setPatientId(9L);
        alert.setAlertType("LOST_FOLLOW_UP");
        alert.setAlertLevel("YELLOW");
        alert.setSourceDueDate(TODAY.minusDays(30));
        alert.setIsResolved(0);
        return alert;
    }
}
