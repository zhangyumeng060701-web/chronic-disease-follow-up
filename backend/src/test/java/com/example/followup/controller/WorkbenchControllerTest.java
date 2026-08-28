package com.example.followup.controller;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUpSuggestion;
import com.example.followup.entity.FollowUpTask;
import com.example.followup.entity.Patient;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.FollowUpSuggestionMapper;
import com.example.followup.mapper.FollowUpTaskMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.security.CurrentUser;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkbenchControllerTest {

    @Mock
    private FollowUpTaskMapper taskMapper;
    @Mock
    private AlertMapper alertMapper;
    @Mock
    private FollowUpSuggestionMapper suggestionMapper;
    @Mock
    private PatientMapper patientMapper;
    @InjectMocks
    private WorkbenchController workbenchController;

    @BeforeAll
    static void initTableMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), Patient.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), FollowUpTask.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), Alert.class);
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(configuration, "test"), FollowUpSuggestion.class);
    }

    @BeforeEach
    void authAsDoctor() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new CurrentUser(7L, "doctor", "DOCTOR"),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doctorWorkbenchOnlyQueriesOwnPatients() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setDoctorId(7L);
        patient.setStatus(1);

        when(patientMapper.selectList(any())).thenReturn(List.of(patient));
        when(taskMapper.selectList(any())).thenReturn(List.of());
        when(alertMapper.selectList(any())).thenReturn(List.of());
        when(suggestionMapper.selectList(any())).thenReturn(List.of());

        Result<Map<String, Object>> result = workbenchController.workbench();

        ArgumentCaptor<Wrapper<FollowUpTask>> taskCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(taskMapper).selectList(taskCaptor.capture());
        assertTrue(taskCaptor.getValue().getSqlSegment().contains("owner_id"));

        ArgumentCaptor<Wrapper<Alert>> alertCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(alertMapper).selectList(alertCaptor.capture());
        assertTrue(alertCaptor.getValue().getSqlSegment().contains("patient_id"));

        ArgumentCaptor<Wrapper<FollowUpSuggestion>> suggestionCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(suggestionMapper).selectList(suggestionCaptor.capture());
        assertTrue(suggestionCaptor.getValue().getSqlSegment().contains("patient_id"));
        assertEquals(List.of(), result.getData().get("pendingAlerts"));
    }

    @Test
    void doctorWorkbenchReturnsEmptyWhenNoPatients() {
        when(patientMapper.selectList(any())).thenReturn(List.of());

        Result<Map<String, Object>> result = workbenchController.workbench();

        assertEquals(List.of(), result.getData().get("todayTasks"));
        assertEquals(List.of(), result.getData().get("pendingAlerts"));
        assertEquals(List.of(), result.getData().get("pendingSuggestions"));
        verifyNoInteractions(taskMapper, alertMapper, suggestionMapper);
    }
}
