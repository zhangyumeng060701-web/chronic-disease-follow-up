package com.example.followup.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.constant.DomainConstants;
import com.example.followup.dto.request.AISuggestionRequest;
import com.example.followup.dto.request.FollowUpInput;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.FollowUpPlan;
import com.example.followup.entity.FollowUpSuggestion;
import com.example.followup.entity.Patient;
import com.example.followup.entity.PatientRiskAssessment;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.FollowUpPlanMapper;
import com.example.followup.mapper.FollowUpSuggestionMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.PatientRiskAssessmentMapper;
import com.example.followup.mapper.PatientVitalMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.ClinicalDecisionServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicalDecisionServiceTest {
    @BeforeAll static void initMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), FollowUpPlan.class);
    }

    @Mock PatientMapper patientMapper;
    @Mock FollowUpMapper followUpMapper;
    @Mock FollowUpPlanMapper planMapper;
    @Mock AlertMapper alertMapper;
    @Mock PatientVitalMapper vitalMapper;
    @Mock PatientRiskAssessmentMapper riskMapper;
    @Mock FollowUpSuggestionMapper suggestionMapper;
    @InjectMocks ClinicalDecisionServiceImpl service;

    @AfterEach void clearContext() { SecurityContextHolder.clearContext(); }

    @Test void riskAssessmentPersistsHighRiskAndUpdatesPlan() {
        authDoctor(7L);
        Patient patient = new Patient(); patient.setId(1L); patient.setAge(66); patient.setDiseaseType(DomainConstants.DISEASE_BOTH);
        Alert red = new Alert(); red.setAlertLevel(DomainConstants.ALERT_LEVEL_RED);
        when(patientMapper.selectById(1L)).thenReturn(patient);
        when(alertMapper.selectList(any())).thenReturn(List.of(red));
        when(vitalMapper.selectList(any())).thenReturn(List.of());

        PatientRiskAssessment result = service.assessPatientRisk(1L);

        assertEquals(DomainConstants.RISK_HIGH, result.getRiskLevel());
        assertEquals(9, result.getScore());
        assertEquals(7L, result.getAssessedBy());
        verify(riskMapper).insert(result);
        verify(planMapper).update(any(), any());
    }

    @Test void aiSuggestionUsesVitalsForRiskConfidenceAndEvidence() {
        FollowUpInput input = new FollowUpInput();
        input.setSystolicBp(181); input.setFastingGlucose(new BigDecimal("12.0"));
        AISuggestionRequest request = new AISuggestionRequest();
        request.setPatientId(3L); request.setRecentFollowUps(List.of(input));

        FollowUpSuggestion result = service.generateAISuggestion(request);

        assertEquals(DomainConstants.RISK_HIGH, result.getRiskLevel());
        assertEquals(new BigDecimal("0.85"), result.getConfidence());
        assertTrue(result.getEvidence().contains("收缩压≥180"));
        assertEquals(DomainConstants.SUGGESTION_STATUS_PENDING, result.getStatus());
        verify(suggestionMapper).insert(result);
    }

    @Test void confirmSuggestionUpdatesStatusAndAppendsFollowUpAdvice() {
        authDoctor(7L);
        FollowUpSuggestion suggestion = suggestion(9L, 4L, "复核用药");
        FollowUp followUp = new FollowUp(); followUp.setId(4L); followUp.setAdvice("控制饮食");
        when(suggestionMapper.selectById(9L)).thenReturn(suggestion);
        when(followUpMapper.selectById(4L)).thenReturn(followUp);

        service.confirmSuggestion(9L);

        assertEquals(DomainConstants.SUGGESTION_STATUS_CONFIRMED, suggestion.getStatus());
        assertEquals(7L, suggestion.getDoctorId());
        assertNotNull(suggestion.getConfirmTime());
        assertEquals("控制饮食；复核用药", followUp.getAdvice());
        verify(followUpMapper).updateById(followUp);
    }

    @Test void rejectSuggestionDoesNotWriteFollowUp() {
        authDoctor(8L);
        FollowUpSuggestion suggestion = suggestion(10L, 5L, "建议内容");
        when(suggestionMapper.selectById(10L)).thenReturn(suggestion);

        service.rejectSuggestion(10L);

        assertEquals(DomainConstants.SUGGESTION_STATUS_REJECTED, suggestion.getStatus());
        assertEquals(8L, suggestion.getDoctorId());
        verify(followUpMapper, never()).updateById(any());
    }

    @Test void missingSuggestionIsRejected() {
        when(suggestionMapper.selectById(99L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> service.confirmSuggestion(99L));
    }

    private FollowUpSuggestion suggestion(Long id, Long followUpId, String content) {
        FollowUpSuggestion value = new FollowUpSuggestion(); value.setId(id); value.setFollowUpId(followUpId); value.setContent(content); return value;
    }

    private void authDoctor(Long id) {
        CurrentUser user = new CurrentUser(id, "doctor", "DOCTOR");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))));
    }
}
