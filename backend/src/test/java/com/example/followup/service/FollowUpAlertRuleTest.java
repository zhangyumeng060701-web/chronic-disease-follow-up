package com.example.followup.service;

import com.example.followup.entity.Alert;
import com.example.followup.entity.AlertRule;
import com.example.followup.entity.FollowUp;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.AlertRuleMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.impl.FollowUpServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowUpAlertRuleTest {
    @Mock FollowUpMapper followUpMapper;
    @Mock PatientMapper patientMapper;
    @Mock AlertRuleMapper alertRuleMapper;
    @Mock AlertMapper alertMapper;
    @InjectMocks FollowUpServiceImpl service;

    static Stream<Arguments> tenRuleBoundaries() {
        return Stream.of(
                Arguments.of("systolic_bp", "180", "179", "RED"),
                Arguments.of("systolic_bp", "160", "159", "YELLOW"),
                Arguments.of("systolic_bp", "140", "139", "YELLOW"),
                Arguments.of("diastolic_bp", "110", "109", "RED"),
                Arguments.of("diastolic_bp", "100", "99", "YELLOW"),
                Arguments.of("diastolic_bp", "90", "89", "YELLOW"),
                Arguments.of("fasting_glucose", "11.1", "11.0", "RED"),
                Arguments.of("fasting_glucose", "7.0", "6.9", "YELLOW"),
                Arguments.of("postprandial_glucose", "16.7", "16.6", "RED"),
                Arguments.of("postprandial_glucose", "11.1", "11.0", "YELLOW")
        );
    }

    @ParameterizedTest(name = "{0} at {1} triggers")
    @MethodSource("tenRuleBoundaries")
    void exactThresholdTriggersAfterTwoConsecutiveReadings(String indicator, String threshold,
                                                            String below, String level) {
        AlertRule rule = rule(indicator, threshold, level);
        FollowUp previous = reading(indicator, threshold);
        prepareInsert(previous, rule);

        service.addFollowUp(reading(indicator, threshold));

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertMapper).insert(captor.capture());
        assertEquals(level, captor.getValue().getAlertLevel());
        assertEquals(0, captor.getValue().getIsResolved());
    }

    @ParameterizedTest(name = "{0} below {1} does not trigger")
    @MethodSource("tenRuleBoundaries")
    void valueImmediatelyBelowThresholdDoesNotTrigger(String indicator, String threshold,
                                                       String below, String level) {
        prepareInsert(reading(indicator, below), rule(indicator, threshold, level));
        service.addFollowUp(reading(indicator, below));
        verify(alertMapper, never()).insert(any());
    }

    @Test
    void firstReadingNeverCreatesConsecutiveAlert() {
        doAnswer(invocation -> { ((FollowUp) invocation.getArgument(0)).setId(2L); return 1; })
                .when(followUpMapper).insert(any(FollowUp.class));
        when(followUpMapper.selectList(any())).thenReturn(Collections.emptyList());
        service.addFollowUp(reading("systolic_bp", "180"));
        verifyNoInteractions(alertRuleMapper, alertMapper);
    }

    @Test
    void missingFollowUpReturns404() {
        when(followUpMapper.selectById(99L)).thenReturn(null);
        BusinessException error = assertThrows(BusinessException.class, () -> service.getFollowUpById(99L));
        assertEquals(404, error.getCode());
    }

    private void prepareInsert(FollowUp previous, AlertRule rule) {
        previous.setId(1L);
        doAnswer(invocation -> { ((FollowUp) invocation.getArgument(0)).setId(2L); return 1; })
                .when(followUpMapper).insert(any(FollowUp.class));
        when(followUpMapper.selectList(any())).thenReturn(List.of(previous));
        when(alertRuleMapper.findActiveRules()).thenReturn(List.of(rule));
    }

    private static AlertRule rule(String indicator, String threshold, String level) {
        AlertRule rule = new AlertRule();
        rule.setRuleName(indicator + " threshold");
        rule.setIndicator(indicator);
        rule.setOperator(">=");
        rule.setThreshold(new BigDecimal(threshold));
        rule.setAlertLevel(level);
        return rule;
    }

    private static FollowUp reading(String indicator, String value) {
        FollowUp followUp = new FollowUp();
        followUp.setPatientId(7L);
        followUp.setFollowUpDate(LocalDate.now());
        if ("systolic_bp".equals(indicator)) followUp.setSystolicBp(new BigDecimal(value).intValue());
        if ("diastolic_bp".equals(indicator)) followUp.setDiastolicBp(new BigDecimal(value).intValue());
        if ("fasting_glucose".equals(indicator)) followUp.setFastingGlucose(new BigDecimal(value));
        if ("postprandial_glucose".equals(indicator)) followUp.setPostprandialGlucose(new BigDecimal(value));
        return followUp;
    }
}
