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
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LostFollowUpAlertExtendedBoundaryTest {
    @BeforeAll
    static void initMetadata() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "lost-alert-extended-test"),
                LostFollowUpAlertRecord.class);
    }

    @Test
    void createsRedAfterThirtyOneDays() {
        LocalDate today = LocalDate.of(2026, 8, 26);
        FollowUp followUp = new FollowUp();
        followUp.setPatientId(10L);
        followUp.setNextFollowUpDate(today.minusDays(31));
        LostFollowUpQueryMapper mapper = mock(LostFollowUpQueryMapper.class);
        when(mapper.findLatestDueFollowUps(any())).thenReturn(List.of(followUp));
        when(mapper.selectList(any(Wrapper.class))).thenReturn(List.of());
        when(mapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(mapper.insert(any(LostFollowUpAlertRecord.class))).thenReturn(1);
        LostFollowUpAlertServiceImpl service = new LostFollowUpAlertServiceImpl(
                mapper, Clock.fixed(Instant.parse("2026-08-26T00:00:00Z"), ZoneOffset.UTC));

        LostFollowUpScanResult result = service.scanAndGenerateAlerts();

        assertEquals(1, result.getRedCreated());
        assertEquals(0, result.getYellowCreated());
    }
}
