package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.constant.DomainConstants;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.FollowUpPlan;
import com.example.followup.entity.FollowUpSuggestion;
import com.example.followup.entity.Patient;
import com.example.followup.entity.PatientRiskAssessment;
import com.example.followup.entity.PatientVital;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.FollowUpPlanMapper;
import com.example.followup.mapper.FollowUpSuggestionMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.PatientRiskAssessmentMapper;
import com.example.followup.mapper.PatientVitalMapper;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.ClinicalDecisionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ClinicalDecisionServiceImpl implements ClinicalDecisionService {

    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private FollowUpMapper followUpMapper;
    @Autowired
    private FollowUpPlanMapper planMapper;
    @Autowired
    private AlertMapper alertMapper;
    @Autowired
    private PatientVitalMapper vitalMapper;
    @Autowired
    private PatientRiskAssessmentMapper riskMapper;
    @Autowired
    private FollowUpSuggestionMapper suggestionMapper;

    @Override
    @Transactional
    public PatientRiskAssessment assessPatientRisk(Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
        int score = 0;
        if (patient.getAge() != null && patient.getAge() >= 65) score += 2;
        if (DomainConstants.DISEASE_BOTH.equals(patient.getDiseaseType())) score += 3;

        List<Alert> unresolved = alertMapper.selectList(new LambdaQueryWrapper<Alert>()
                .eq(Alert::getPatientId, patientId)
                .eq(Alert::getIsResolved, 0)
                .orderByDesc(Alert::getCreateTime)
                .last("LIMIT 5"));
        long redCount = unresolved.stream().filter(a -> DomainConstants.ALERT_LEVEL_RED.equals(a.getAlertLevel())).count();
        long yellowCount = unresolved.stream().filter(a -> DomainConstants.ALERT_LEVEL_YELLOW.equals(a.getAlertLevel())).count();
        score += redCount * 4;
        score += yellowCount * 2;

        List<PatientVital> latestVitals = vitalMapper.selectList(new LambdaQueryWrapper<PatientVital>()
                .eq(PatientVital::getPatientId, patientId)
                .orderByDesc(PatientVital::getMeasuredAt)
                .last("LIMIT 4"));
        for (PatientVital vital : latestVitals) {
            if (isSevereVital(vital)) score += 3;
            else if (isModerateVital(vital)) score += 1;
        }

        String riskLevel = score >= 5 ? DomainConstants.RISK_HIGH
                : score >= 2 ? DomainConstants.RISK_MEDIUM : DomainConstants.RISK_STABLE;

        PatientRiskAssessment assessment = new PatientRiskAssessment();
        assessment.setPatientId(patientId);
        assessment.setRiskLevel(riskLevel);
        assessment.setScore(score);
        assessment.setEvidence("年龄/合并症/未处理预警/近期指标综合评分：" + score);
        assessment.setAssessedBy(SecurityUtils.currentUser().getUserId());
        assessment.setAssessedAt(LocalDateTime.now());
        riskMapper.insert(assessment);

        planMapper.update(null, new LambdaUpdateWrapper<FollowUpPlan>()
                .eq(FollowUpPlan::getPatientId, patientId)
                .eq(FollowUpPlan::getStatus, DomainConstants.PLAN_STATUS_ACTIVE)
                .set(FollowUpPlan::getRiskLevel, riskLevel));
        log.info("assessPatientRisk patientId={} score={} level={}", patientId, score, riskLevel);
        return assessment;
    }

    @Override
    @Transactional
    public FollowUpSuggestion generateSuggestion(Long patientId) {
        PatientRiskAssessment latest = riskMapper.selectOne(new LambdaQueryWrapper<PatientRiskAssessment>()
                .eq(PatientRiskAssessment::getPatientId, patientId)
                .orderByDesc(PatientRiskAssessment::getAssessedAt)
                .last("LIMIT 1"));
        FollowUp latestFollowUp = followUpMapper.selectOne(new LambdaQueryWrapper<FollowUp>()
                .eq(FollowUp::getPatientId, patientId)
                .orderByDesc(FollowUp::getFollowUpDate)
                .last("LIMIT 1"));
        String riskText = latest == null ? "暂未分层" : latest.getRiskLevel();
        String content = String.format("患者当前风险分层为%s，建议%s%s。",
                riskText,
                DomainConstants.RISK_HIGH.equals(riskText) ? "缩短随访间隔并优先复核用药与靶器官风险" : "按计划随访并持续监测",
                latestFollowUp == null ? "" : "，参考最近随访日期 " + latestFollowUp.getFollowUpDate());
        FollowUpSuggestion suggestion = new FollowUpSuggestion();
        suggestion.setPatientId(patientId);
        suggestion.setFollowUpId(latestFollowUp == null ? null : latestFollowUp.getId());
        suggestion.setContent(content);
        suggestion.setSource("AI");
        suggestion.setStatus(DomainConstants.SUGGESTION_STATUS_PENDING);
        suggestionMapper.insert(suggestion);
        log.info("generateSuggestion patientId={} id={}", patientId, suggestion.getId());
        return suggestion;
    }

    @Override
    public PageResponse<FollowUpSuggestion> listSuggestions(Integer page, Integer size, String status) {
        LambdaQueryWrapper<FollowUpSuggestion> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(FollowUpSuggestion::getStatus, status);
        }
        wrapper.orderByDesc(FollowUpSuggestion::getCreateTime);
        Page<FollowUpSuggestion> p = new Page<>(page, size);
        suggestionMapper.selectPage(p, wrapper);
        return PageResponseUtil.of(p, p.getRecords(), page, size);
    }

    @Override
    @Transactional
    public void confirmSuggestion(Long id) {
        FollowUpSuggestion suggestion = getSuggestionOrThrow(id);
        suggestion.setStatus(DomainConstants.SUGGESTION_STATUS_CONFIRMED);
        suggestion.setDoctorId(SecurityUtils.currentUser().getUserId());
        suggestion.setConfirmTime(LocalDateTime.now());
        suggestionMapper.updateById(suggestion);
        if (suggestion.getFollowUpId() != null) {
            FollowUp followUp = followUpMapper.selectById(suggestion.getFollowUpId());
            if (followUp != null) {
                String advice = followUp.getAdvice() == null ? "" : followUp.getAdvice();
                followUp.setAdvice(StringUtils.hasText(advice) ? advice + "；" + suggestion.getContent() : suggestion.getContent());
                followUpMapper.updateById(followUp);
            }
        }
        log.info("confirmSuggestion id={}", id);
    }

    @Override
    public void rejectSuggestion(Long id) {
        FollowUpSuggestion suggestion = getSuggestionOrThrow(id);
        suggestion.setStatus(DomainConstants.SUGGESTION_STATUS_REJECTED);
        suggestion.setDoctorId(SecurityUtils.currentUser().getUserId());
        suggestion.setConfirmTime(LocalDateTime.now());
        suggestionMapper.updateById(suggestion);
        log.info("rejectSuggestion id={}", id);
    }

    @Scheduled(cron = "0 10 2 * * ?")
    public void assessAllPatientsDaily() {
        List<Patient> patients = patientMapper.selectList(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getStatus, 1));
        patients.forEach(patient -> assessPatientRisk(patient.getId()));
        log.info("assessAllPatientsDaily size={}", patients.size());
    }

    private FollowUpSuggestion getSuggestionOrThrow(Long id) {
        FollowUpSuggestion suggestion = suggestionMapper.selectById(id);
        if (suggestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "随访建议不存在");
        }
        return suggestion;
    }

    private boolean isSevereVital(PatientVital vital) {
        BigDecimal value = vital.getMetricValue();
        if (value == null) return false;
        switch (vital.getMetricType()) {
            case DomainConstants.METRIC_SYSTOLIC_BP:
                return value.compareTo(new BigDecimal("180")) >= 0;
            case DomainConstants.METRIC_DIASTOLIC_BP:
                return value.compareTo(new BigDecimal("110")) >= 0;
            case DomainConstants.METRIC_FASTING_GLUCOSE:
                return value.compareTo(new BigDecimal("11.1")) >= 0;
            case DomainConstants.METRIC_POSTPRANDIAL_GLUCOSE:
                return value.compareTo(new BigDecimal("16.7")) >= 0;
            default:
                return false;
        }
    }

    private boolean isModerateVital(PatientVital vital) {
        BigDecimal value = vital.getMetricValue();
        if (value == null) return false;
        switch (vital.getMetricType()) {
            case DomainConstants.METRIC_SYSTOLIC_BP:
                return value.compareTo(new BigDecimal("140")) >= 0;
            case DomainConstants.METRIC_DIASTOLIC_BP:
                return value.compareTo(new BigDecimal("90")) >= 0;
            case DomainConstants.METRIC_FASTING_GLUCOSE:
                return value.compareTo(new BigDecimal("7.0")) >= 0;
            case DomainConstants.METRIC_POSTPRANDIAL_GLUCOSE:
                return value.compareTo(new BigDecimal("11.1")) >= 0;
            default:
                return false;
        }
    }
}
