package com.example.followup.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.dto.request.FollowUpQuery;
import com.example.followup.entity.AlertRule;
import com.example.followup.entity.FollowUp;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.AlertRuleMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.impl.FollowUpServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowUpServiceTest {

    @Mock
    private FollowUpMapper followUpMapper;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private AlertRuleMapper alertRuleMapper;
    @Mock
    private AlertMapper alertMapper;
    @InjectMocks
    private FollowUpServiceImpl followUpService;

    @BeforeAll
    static void initTableMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), FollowUp.class);
    }

    @Test
    @DisplayName("开始日期晚于结束日期时拒绝查询")
    void rejectsInvertedDateRange() {
        FollowUpQuery query = new FollowUpQuery();
        query.setStartDate(LocalDate.of(2026, 8, 10));
        query.setEndDate(LocalDate.of(2026, 8, 1));

        BusinessException exception = assertThrows(BusinessException.class, () -> followUpService.listFollowUps(query));
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    @DisplayName("不存在的随访记录返回 404")
    void getFollowUpByIdNotFound() {
        when(followUpMapper.selectById(99L)).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> followUpService.getFollowUpById(99L)
        );
        assertEquals(404, exception.getHttpStatus());
    }

    @Test
    @DisplayName("没有历史随访时不生成预警")
    void addFollowUpWithoutPreviousDoesNotGenerateAlert() {
        FollowUp followUp = new FollowUp();
        followUp.setPatientId(1L);
        when(followUpMapper.selectList(any())).thenReturn(Collections.emptyList());

        followUpService.addFollowUp(followUp);

        verify(followUpMapper).insert(followUp);
        verify(alertMapper, never()).insert(any());
        verify(alertMapper, never()).batchInsert(anyList());
    }

    @Test
    @DisplayName("连续两次异常时批量生成预警")
    void addFollowUpWithContinuousAbnormalGeneratesBatchAlerts() {
        FollowUp previous = followUp(1L, 1L, 180);
        FollowUp current = followUp(2L, 1L, 185);
        when(followUpMapper.selectList(any())).thenReturn(List.of(previous));

        AlertRule rule = new AlertRule();
        rule.setRuleName("收缩压高危");
        rule.setIndicator("systolic_bp");
        rule.setThreshold(new BigDecimal("180"));
        rule.setAlertLevel("RED");
        when(alertRuleMapper.findActiveRules()).thenReturn(List.of(rule));

        followUpService.addFollowUp(current);

        verify(alertMapper).batchInsert(anyList());
    }

    @Test
    @DisplayName("低于阈值时连续两次也不生成预警")
    void addFollowUpBelowThresholdDoesNotGenerateAlert() {
        FollowUp previous = followUp(1L, 1L, 179);
        FollowUp current = followUp(2L, 1L, 179);
        when(followUpMapper.selectList(any())).thenReturn(List.of(previous));

        AlertRule rule = new AlertRule();
        rule.setRuleName("收缩压高危");
        rule.setIndicator("systolic_bp");
        rule.setThreshold(new BigDecimal("180"));
        rule.setAlertLevel("RED");
        when(alertRuleMapper.findActiveRules()).thenReturn(List.of(rule));

        followUpService.addFollowUp(current);

        verify(alertMapper, never()).insert(any());
        verify(alertMapper, never()).batchInsert(anyList());
    }

    private FollowUp followUp(Long id, Long patientId, int systolicBp) {
        FollowUp followUp = new FollowUp();
        followUp.setId(id);
        followUp.setPatientId(patientId);
        followUp.setSystolicBp(systolicBp);
        return followUp;
    }
}
