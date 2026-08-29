/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.constant.DomainConstants;
import com.example.followup.dto.request.PatientFollowUpRequest;
import com.example.followup.dto.request.QuestionnaireSubmitRequest;
import com.example.followup.dto.request.VitalReportRequest;
import com.example.followup.dto.response.FollowUpPlanVO;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.FollowUpPlan;
import com.example.followup.entity.Message;
import com.example.followup.entity.Patient;
import com.example.followup.entity.PatientRiskAssessment;
import com.example.followup.entity.PatientVital;
import com.example.followup.entity.Questionnaire;
import com.example.followup.entity.QuestionnaireSubmission;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.FollowUpPlanMapper;
import com.example.followup.mapper.MessageMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.PatientVitalMapper;
import com.example.followup.mapper.QuestionnaireMapper;
import com.example.followup.mapper.QuestionnaireSubmissionMapper;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.PatientPortalService;
import com.example.followup.util.VoMappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PatientPortalServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class PatientPortalServiceImpl implements PatientPortalService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private FollowUpPlanMapper planMapper;
    @Autowired
    private FollowUpMapper followUpMapper;
    @Autowired
    private PatientVitalMapper vitalMapper;
    @Autowired
    private QuestionnaireMapper questionnaireMapper;
    @Autowired
    private QuestionnaireSubmissionMapper submissionMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private AlertMapper alertMapper;
    @Autowired
    private com.example.followup.mapper.PatientRiskAssessmentMapper riskAssessmentMapper;

    /**
     * 执行 myPlans 操作。
     */
    @Override
    public List<FollowUpPlanVO> myPlans() {
        Long patientId = SecurityUtils.patientId();
        List<FollowUpPlan> plans = planMapper.selectList(new LambdaQueryWrapper<FollowUpPlan>()
                .eq(FollowUpPlan::getPatientId, patientId)
                .eq(FollowUpPlan::getStatus, DomainConstants.PLAN_STATUS_ACTIVE)
                .orderByAsc(FollowUpPlan::getNextFollowUpDate));
        Patient patient = patientMapper.selectById(patientId);
        return plans.stream().map(plan -> {
            FollowUpPlanVO vo = new FollowUpPlanVO();
            vo.setId(plan.getId());
            vo.setPatientId(plan.getPatientId());
            vo.setPatientName(patient == null ? "" : patient.getName());
            vo.setRiskLevel(plan.getRiskLevel());
            vo.setFollowUpFrequencyDays(plan.getFollowUpFrequencyDays());
            vo.setFollowUpType(plan.getFollowUpType());
            vo.setNextFollowUpDate(plan.getNextFollowUpDate());
            vo.setStatus(plan.getStatus());
            vo.setDoctorId(plan.getDoctorId());
            vo.setRemark(plan.getRemark());
            vo.setCreateTime(plan.getCreateTime());
            vo.setUpdateTime(plan.getUpdateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 执行 myVitals 操作。
     */
    @Override
    public List<PatientVital> myVitals() {
        Long patientId = SecurityUtils.patientId();
        return vitalMapper.selectList(new LambdaQueryWrapper<PatientVital>()
                .eq(PatientVital::getPatientId, patientId)
                .orderByDesc(PatientVital::getMeasuredAt));
    }

    @Override
    @Transactional
    public PatientVital reportVital(VitalReportRequest request) {
        Long patientId = SecurityUtils.patientId();
        PatientVital vital = new PatientVital();
        vital.setPatientId(patientId);
        vital.setMetricType(request.getMetricType());
        vital.setMetricValue(request.getMetricValue());
        vital.setMeasuredAt(request.getMeasuredAt());
        vital.setSourceType(DomainConstants.VITAL_SOURCE_PATIENT);
        vital.setRemark(request.getRemark());
        vitalMapper.insert(vital);

        generateAlertFromVital(patientId, vital);
        log.info("reportVital patientId={} metric={} value={}", patientId, vital.getMetricType(), vital.getMetricValue());
        return vital;
    }

    /**
     * 执行 activeQuestionnaires 操作。
     */
    @Override
    public List<Questionnaire> activeQuestionnaires() {
        return questionnaireMapper.selectList(new LambdaQueryWrapper<Questionnaire>()
                .eq(Questionnaire::getIsActive, 1)
                .orderByDesc(Questionnaire::getCreateTime));
    }

    @Override
    @Transactional
    public void submitQuestionnaire(Long questionnaireId, QuestionnaireSubmitRequest request) {
        Questionnaire questionnaire = questionnaireMapper.selectById(questionnaireId);
        if (questionnaire == null || questionnaire.getIsActive() == null || questionnaire.getIsActive() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "问卷不存在或已停用");
        }
        try {
            QuestionnaireSubmission submission = new QuestionnaireSubmission();
            submission.setQuestionnaireId(questionnaireId);
            submission.setPatientId(SecurityUtils.patientId());
            submission.setAnswerJson(objectMapper.writeValueAsString(request.getAnswers()));
            submissionMapper.insert(submission);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "问卷答案格式不正确");
        }
    }

    /**
     * 执行 myMessages 操作。
     */
    @Override
    public List<Message> myMessages() {
        Long patientId = SecurityUtils.patientId();
        return messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getRecipientType, DomainConstants.ROLE_PATIENT)
                .eq(Message::getRecipientId, patientId)
                .orderByDesc(Message::getCreateTime));
    }

    @Override
    public long unreadMessageCount() {
        Long patientId = SecurityUtils.patientId();
        return messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                .eq(Message::getRecipientType, DomainConstants.ROLE_PATIENT)
                .eq(Message::getRecipientId, patientId)
                .eq(Message::getStatus, DomainConstants.MESSAGE_STATUS_PENDING));
    }

    /**
     * 执行 markMessageRead 操作。
     */
    @Override
    public void markMessageRead(Long id) {
        Message message = messageMapper.selectById(id);
        if (message == null || !DomainConstants.ROLE_PATIENT.equals(message.getRecipientType())
                || !SecurityUtils.patientId().equals(message.getRecipientId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "消息不存在");
        }
        message.setStatus(DomainConstants.MESSAGE_STATUS_READ);
        message.setReadTime(LocalDateTime.now());
        messageMapper.updateById(message);
    }

    /**
     * 执行 myFollowUps 操作。
     */
    @Override
    public List<FollowUpVO> myFollowUps() {
        Long patientId = SecurityUtils.patientId();
        List<FollowUp> records = followUpMapper.selectList(new LambdaQueryWrapper<FollowUp>()
                .eq(FollowUp::getPatientId, patientId)
                .orderByDesc(FollowUp::getFollowUpDate));
        return records.stream().map(VoMappers::toFollowUpVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FollowUpVO createPatientFollowUp(PatientFollowUpRequest request) {
        Long patientId = SecurityUtils.patientId();
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
        FollowUp followUp = new FollowUp();
        followUp.setPatientId(patientId);
        followUp.setDoctorId(patient.getDoctorId());
        followUp.setFollowUpDate(request.getFollowUpDate());
        followUp.setFollowUpType(DomainConstants.FOLLOW_UP_TYPE_PATIENT);
        followUp.setSourceType(DomainConstants.FOLLOW_UP_SOURCE_PATIENT);
        followUp.setSystolicBp(request.getSystolicBp());
        followUp.setDiastolicBp(request.getDiastolicBp());
        followUp.setFastingGlucose(request.getFastingGlucose());
        followUp.setPostprandialGlucose(request.getPostprandialGlucose());
        followUp.setMedicationAdherence(request.getMedicationAdherence());
        followUp.setSymptoms(request.getSymptoms());
        followUp.setAdvice(request.getAdvice());
        followUp.setNextFollowUpDate(request.getNextFollowUpDate());
        followUpMapper.insert(followUp);
        log.info("createPatientFollowUp patientId={} id={}", patientId, followUp.getId());
        return VoMappers.toFollowUpVO(followUp);
    }

    /**
     * 执行 myRiskLevel 操作。
     */
    @Override
    public Map<String, Object> myRiskLevel() {
        Long patientId = SecurityUtils.patientId();
        PatientRiskAssessment latest = riskAssessmentMapper.selectOne(new LambdaQueryWrapper<PatientRiskAssessment>()
                .eq(PatientRiskAssessment::getPatientId, patientId)
                .orderByDesc(PatientRiskAssessment::getAssessedAt)
                .last("LIMIT 1"));
        String riskLevel = latest == null ? DomainConstants.RISK_STABLE : latest.getRiskLevel();
        if (latest == null) {
            FollowUpPlan plan = planMapper.selectOne(new LambdaQueryWrapper<FollowUpPlan>()
                    .eq(FollowUpPlan::getPatientId, patientId)
                    .eq(FollowUpPlan::getStatus, DomainConstants.PLAN_STATUS_ACTIVE)
                    .orderByDesc(FollowUpPlan::getUpdateTime)
                    .last("LIMIT 1"));
            if (plan != null && StringUtils.hasText(plan.getRiskLevel())) {
                riskLevel = plan.getRiskLevel();
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("riskLevel", riskLevel);
        result.put("score", latest == null ? 0 : latest.getScore());
        result.put("evidence", latest == null ? "暂无正式评估，使用计划风险等级" : latest.getEvidence());
        return result;
    }

    private void generateAlertFromVital(Long patientId, PatientVital vital) {
        Optional<String> levelOpt = resolveVitalAlertLevel(vital.getMetricType(), vital.getMetricValue());
        if (levelOpt.isEmpty()) {
            return;
        }
        String level = levelOpt.get();
        Alert alert = new Alert();
        alert.setPatientId(patientId);
        alert.setAlertType(DomainConstants.ALERT_TYPE_HIGH_RISK);
        alert.setAlertLevel(level);
        alert.setAlertReason("患者自测" + metricLabel(vital.getMetricType()) + " " + vital.getMetricValue()
                + "，触发" + (DomainConstants.ALERT_LEVEL_RED.equals(level) ? "红色" : "黄色") + "预警");
        alert.setIsResolved(0);
        alert.setAlertStatus(DomainConstants.ALERT_STATUS_PENDING);
        boolean red = DomainConstants.ALERT_LEVEL_RED.equals(level);
        alert.setRecommendedActions(red ? "尽快复诊并复核用药方案" : "加强监测并评估用药与生活方式");
        alert.setRecheckItems(red ? "血压、血糖、肾功能" : "血压、血糖");
        alert.setReferralConditions(red ? "持续异常或出现明显不适" : "多次测量仍高于目标值");
        alert.setEvidenceSource("基层慢病随访管理规范");
        alert.setRiskLevel(red ? DomainConstants.RISK_HIGH : DomainConstants.RISK_MEDIUM);
        alertMapper.insert(alert);
    }

    private Optional<String> resolveVitalAlertLevel(String metricType, BigDecimal value) {
        if (value == null) {
            return Optional.empty();
        }
        switch (metricType) {
            case DomainConstants.METRIC_SYSTOLIC_BP:
                if (value.compareTo(new BigDecimal("180")) >= 0) {
                    return Optional.of(DomainConstants.ALERT_LEVEL_RED);
                }
                if (value.compareTo(new BigDecimal("140")) >= 0) {
                    return Optional.of(DomainConstants.ALERT_LEVEL_YELLOW);
                }
                return Optional.empty();
            case DomainConstants.METRIC_DIASTOLIC_BP:
                if (value.compareTo(new BigDecimal("110")) >= 0) {
                    return Optional.of(DomainConstants.ALERT_LEVEL_RED);
                }
                if (value.compareTo(new BigDecimal("90")) >= 0) {
                    return Optional.of(DomainConstants.ALERT_LEVEL_YELLOW);
                }
                return Optional.empty();
            case DomainConstants.METRIC_FASTING_GLUCOSE:
                if (value.compareTo(new BigDecimal("11.1")) >= 0) {
                    return Optional.of(DomainConstants.ALERT_LEVEL_RED);
                }
                if (value.compareTo(new BigDecimal("7.0")) >= 0) {
                    return Optional.of(DomainConstants.ALERT_LEVEL_YELLOW);
                }
                return Optional.empty();
            case DomainConstants.METRIC_POSTPRANDIAL_GLUCOSE:
                if (value.compareTo(new BigDecimal("16.7")) >= 0) {
                    return Optional.of(DomainConstants.ALERT_LEVEL_RED);
                }
                if (value.compareTo(new BigDecimal("11.1")) >= 0) {
                    return Optional.of(DomainConstants.ALERT_LEVEL_YELLOW);
                }
                return Optional.empty();
            default:
                return Optional.empty();
        }
    }

    private String metricLabel(String metricType) {
        switch (metricType) {
            case DomainConstants.METRIC_SYSTOLIC_BP: return "收缩压";
            case DomainConstants.METRIC_DIASTOLIC_BP: return "舒张压";
            case DomainConstants.METRIC_FASTING_GLUCOSE: return "空腹血糖";
            case DomainConstants.METRIC_POSTPRANDIAL_GLUCOSE: return "餐后血糖";
            default: return metricType;
        }
    }
}
