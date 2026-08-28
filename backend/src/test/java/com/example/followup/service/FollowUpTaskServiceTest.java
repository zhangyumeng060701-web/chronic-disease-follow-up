/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.entity.FollowUpPlan;
import com.example.followup.entity.FollowUpTask;
import com.example.followup.mapper.FollowUpPlanMapper;
import com.example.followup.mapper.FollowUpTaskMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.FollowUpTaskServiceImpl;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FollowUpTaskServiceTest {
    @Mock
    private FollowUpTaskMapper taskMapper;
    @Mock
    private FollowUpPlanMapper planMapper;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private SysUserMapper sysUserMapper;
    @InjectMocks
    private FollowUpTaskServiceImpl taskService;

    @BeforeAll
    static void initTableMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), FollowUpTask.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), FollowUpPlan.class);
    }

    @BeforeEach
    void authAsAdmin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new CurrentUser(1L, "admin", "ADMIN"),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("从计划创建首次随访任务")
    void createTaskFromPlanInsertsTask() {
        FollowUpPlan plan = plan(1L, 1L, LocalDate.now());
        when(taskMapper.selectCount(any())).thenReturn(0L);

        taskService.createTaskFromPlan(plan);

        verify(taskMapper).insert(any(FollowUpTask.class));
    }

    @Test
    @DisplayName("同计划同日期已有未完成任务时不重复创建")
    void createTaskFromPlanSkipsDuplicate() {
        FollowUpPlan plan = plan(1L, 1L, LocalDate.now());
        when(taskMapper.selectCount(any())).thenReturn(1L);

        taskService.createTaskFromPlan(plan);

        verify(taskMapper, never()).insert(any());
    }

    @Test
    @DisplayName("完成任务后按计划频率推进下次随访日期")
    void completeTaskAdvancesPlanNextDate() {
        FollowUpTask task = new FollowUpTask();
        task.setId(1L);
        task.setPlanId(1L);
        task.setStatus("PENDING");

        FollowUpPlan plan = plan(1L, 1L, LocalDate.now());
        plan.setFollowUpFrequencyDays(14);
        when(taskMapper.selectById(1L)).thenReturn(task);
        when(planMapper.selectById(1L)).thenReturn(plan);

        taskService.completeTask(1L);

        assertEquals("COMPLETED", task.getStatus());
        assertEquals(LocalDate.now().plusDays(14), plan.getNextFollowUpDate());
        verify(taskMapper).updateById(task);
        verify(planMapper).updateById(plan);
    }

    private FollowUpPlan plan(Long id, Long patientId, LocalDate nextDate) {
        FollowUpPlan plan = new FollowUpPlan();
        plan.setId(id);
        plan.setPatientId(patientId);
        plan.setDoctorId(1L);
        plan.setRiskLevel("MEDIUM");
        plan.setFollowUpFrequencyDays(14);
        plan.setFollowUpType("电话");
        plan.setNextFollowUpDate(nextDate);
        plan.setStatus("ACTIVE");
        return plan;
    }
}
