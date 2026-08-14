package com.example.followup.service;

import com.example.followup.dto.response.StatsOverview;
import com.example.followup.mapper.*;
import com.example.followup.service.impl.StatsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {
    @Mock PatientMapper patientMapper;
    @Mock FollowUpMapper followUpMapper;
    @Mock AlertMapper alertMapper;
    @Mock SysUserMapper sysUserMapper;
    @InjectMocks StatsServiceImpl service;

    @Test void overviewCalculatesCompletionRate() {
        when(patientMapper.selectCount(any())).thenReturn(4L);
        when(followUpMapper.countMonthlyCompleted()).thenReturn(3);
        when(alertMapper.countHighRisk()).thenReturn(2L);
        when(alertMapper.countLostFollowUp()).thenReturn(1L);
        StatsOverview result = service.getOverview();
        assertEquals(4L, result.getTotalPatients());
        assertEquals("75.0%", result.getCompletionRate());
        assertEquals(2L, result.getHighRiskCount());
    }

    @Test void overviewHandlesNullCountsAndZeroDenominator() {
        when(patientMapper.selectCount(any())).thenReturn(null);
        when(followUpMapper.countMonthlyCompleted()).thenReturn(null);
        StatsOverview result = service.getOverview();
        assertEquals(0L, result.getTotalPatients());
        assertEquals("-", result.getCompletionRate());
    }
}
