package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.FollowUpQuery;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.constant.DomainConstants;
import com.example.followup.entity.Alert;
import com.example.followup.entity.AlertRule;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.AlertRuleMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.FollowUpService;
import com.example.followup.util.DesensitizationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FollowUpServiceImpl implements FollowUpService {

    @Autowired
    private FollowUpMapper followUpMapper;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private AlertRuleMapper alertRuleMapper;
    @Autowired
    private AlertMapper alertMapper;

    @Override
    public PageResponse<FollowUpVO> listFollowUps(FollowUpQuery query) {
        if (query.getStartDate() != null && query.getEndDate() != null
                && query.getStartDate().isAfter(query.getEndDate())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "开始日期不能晚于结束日期");
        }
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        boolean admin = SecurityUtils.isAdmin();
        if (!admin) {
            wrapper.eq(FollowUp::getDoctorId, SecurityUtils.currentUser().getUserId());
        }
        if (query.getPatientId() != null) {
            wrapper.eq(FollowUp::getPatientId, query.getPatientId());
        }
        if (query.getStartDate() != null) {
            wrapper.ge(FollowUp::getFollowUpDate, query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le(FollowUp::getFollowUpDate, query.getEndDate());
        }
        if (query.getNextFollowUpDateBefore() != null) {
            wrapper.le(FollowUp::getNextFollowUpDate, query.getNextFollowUpDateBefore());
        }
        wrapper.orderByDesc(FollowUp::getFollowUpDate);

        Page<FollowUp> page = new Page<>(query.getPage(), query.getSize());
        followUpMapper.selectPage(page, wrapper);

        List<Long> patientIds = page.getRecords().stream()
                .map(FollowUp::getPatientId).distinct().collect(Collectors.toList());
        Map<Long, String> nameMap = patientIds.isEmpty() ? Map.of() :
                patientMapper.selectBatchIds(patientIds).stream()
                        .collect(Collectors.toMap(Patient::getId, Patient::getName));

        List<FollowUpVO> vos = page.getRecords().stream().map(f -> {
            FollowUpVO vo = new FollowUpVO();
            BeanUtils.copyProperties(f, vo);
            String patientName = nameMap.getOrDefault(f.getPatientId(), "");
            vo.setPatientName(admin ? patientName : DesensitizationUtil.maskName(patientName));
            return vo;
        }).collect(Collectors.toList());

        return PageResponseUtil.of(page, vos, query.getPage(), query.getSize());
    }

    @Override
    public FollowUp getFollowUpById(Long id) {
        FollowUp followUp = followUpMapper.selectById(id);
        if (followUp == null) {
            throw new BusinessException(ErrorCode.FOLLOWUP_NOT_FOUND);
        }
        if (!SecurityUtils.isAdmin()
                && !Objects.equals(followUp.getDoctorId(), SecurityUtils.currentUser().getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return followUp;
    }

    @Override
    @Transactional
    public void addFollowUp(FollowUp followUp) {
        followUp.setId(null);
        followUpMapper.insert(followUp);
        checkAndGenerateAlerts(followUp);
    }

    @Override
    @Transactional
    public void updateFollowUp(FollowUp followUp) {
        getFollowUpById(followUp.getId());
        followUpMapper.updateById(followUp);
    }

    @Override
    @Transactional
    public void deleteFollowUp(Long id) {
        getFollowUpById(id);
        followUpMapper.deleteById(id);
    }

    @Override
    public List<FollowUpVO> listOverdueFollowUps() {
        FollowUpQuery query = new FollowUpQuery();
        query.setPage(1);
        query.setSize(Integer.MAX_VALUE);
        // 超期 7 天以上：下次随访日期早于 7 天前
        query.setNextFollowUpDateBefore(LocalDate.now().minusDays(7));
        return listFollowUps(query).getRecords();
    }

    // ---- 连续异常预警 ----

    private void checkAndGenerateAlerts(FollowUp followUp) {
        Long patientId = followUp.getPatientId();

        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowUp::getPatientId, patientId)
               .lt(FollowUp::getId, followUp.getId())
               .orderByDesc(FollowUp::getFollowUpDate)
               .last("LIMIT 1");
        FollowUp previous = followUpMapper.selectList(wrapper).stream().findFirst().orElse(null);
        if (previous == null) return;

        List<AlertRule> rules = alertRuleMapper.findActiveRules();
        List<Alert> alerts = new ArrayList<>();

        for (AlertRule rule : rules) {
            boolean currentTriggered = checkIndicator(followUp, rule);
            boolean previousTriggered = checkIndicator(previous, rule);
            if (currentTriggered && previousTriggered) {
                Alert alert = new Alert();
                alert.setPatientId(patientId);
                alert.setAlertType(DomainConstants.ALERT_TYPE_HIGH_RISK);
                alert.setAlertLevel(rule.getAlertLevel());
                alert.setAlertReason("连续2次" + rule.getRuleName() + "：最近值" + getIndicatorValue(followUp, rule.getIndicator()));
                alert.setIsResolved(0);
                alerts.add(alert);
            }
        }

        if (!alerts.isEmpty()) {
            alertMapper.batchInsert(alerts);
        }
    }

    private boolean checkIndicator(FollowUp f, AlertRule rule) {
        BigDecimal value = getIndicatorValue(f, rule.getIndicator());
        if (value == null) return false;
        return value.compareTo(rule.getThreshold()) >= 0;
    }

    private BigDecimal getIndicatorValue(FollowUp f, String indicator) {
        switch (indicator) {
            case "systolic_bp": return f.getSystolicBp() != null ? BigDecimal.valueOf(f.getSystolicBp()) : null;
            case "diastolic_bp": return f.getDiastolicBp() != null ? BigDecimal.valueOf(f.getDiastolicBp()) : null;
            case "fasting_glucose": return f.getFastingGlucose();
            case "postprandial_glucose": return f.getPostprandialGlucose();
            default: return null;
        }
    }
}
