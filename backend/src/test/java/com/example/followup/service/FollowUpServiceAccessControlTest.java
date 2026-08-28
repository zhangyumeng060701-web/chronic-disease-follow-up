/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.followup.engine.AlertRuleEngine;
import com.example.followup.entity.FollowUp;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.AlertRuleMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.FollowUpServiceImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class FollowUpServiceAccessControlTest {
    @Mock
    private FollowUpMapper followUpMapper;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private AlertRuleMapper alertRuleMapper;
    @Mock
    private AlertMapper alertMapper;
    @Mock
    private AlertRuleEngine alertRuleEngine;
    @InjectMocks
    private FollowUpServiceImpl followUpService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("医生可以读取自己创建的随访记录")
    void doctorCanReadOwnFollowUp() {
        authAsDoctor(7L);
        when(followUpMapper.selectById(1L)).thenReturn(followUp(1L, 7L));

        assertEquals(1L, followUpService.getFollowUpById(1L).getId());
    }

    @Test
    @DisplayName("医生不能读取其他医生的随访记录")
    void doctorCannotReadOtherDoctorsFollowUp() {
        authAsDoctor(7L);
        when(followUpMapper.selectById(1L)).thenReturn(followUp(1L, 8L));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> followUpService.getFollowUpById(1L)
        );
        assertEquals(403, exception.getHttpStatus());
    }

    private void authAsDoctor(Long userId) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new CurrentUser(userId, "doctor", "DOCTOR"),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );
    }

    private FollowUp followUp(Long id, Long doctorId) {
        FollowUp followUp = new FollowUp();
        followUp.setId(id);
        followUp.setDoctorId(doctorId);
        return followUp;
    }
}
