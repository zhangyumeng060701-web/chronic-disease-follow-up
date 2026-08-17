package com.example.followup.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.dto.response.StatsOverview;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.StatsServiceImpl;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private PatientMapper patientMapper;
    @Mock
    private FollowUpMapper followUpMapper;
    @Mock
    private AlertMapper alertMapper;
    @Mock
    private SysUserMapper sysUserMapper;
    @InjectMocks
    private StatsServiceImpl statsService;

    @BeforeAll
    static void initTableMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), Patient.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), FollowUp.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), Alert.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), SysUser.class);
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
    @DisplayName("管理员总览统计正确")
    void overviewCalculatesCompletionRate() {
        when(patientMapper.selectCount(any())).thenReturn(4L);
        when(followUpMapper.selectCount(any())).thenReturn(3L);
        when(alertMapper.countHighRisk()).thenReturn(2L);
        when(alertMapper.countLostFollowUp()).thenReturn(1L);

        StatsOverview result = statsService.getOverview();

        assertEquals(4L, result.getTotalPatients());
        assertEquals(3, result.getMonthlyCompleted());
        assertEquals("75.0%", result.getCompletionRate());
        assertEquals(2L, result.getHighRiskCount());
        assertEquals(1L, result.getLostFollowUpCount());
    }
}
