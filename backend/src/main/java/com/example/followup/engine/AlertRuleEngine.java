/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.engine;

import com.example.followup.constant.DomainConstants;
import com.example.followup.entity.Alert;
import com.example.followup.entity.AlertRule;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * AlertRuleEngine 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Component
public class AlertRuleEngine {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 兼容旧调用：仅执行阈值规则。
     */
    public List<Alert> evaluate(FollowUp current, FollowUp previous, List<AlertRule> rules) {
        return evaluate(current, previous, null, rules);
    }

    /**
     * 按阈值、趋势、合并症、用药四类规则生成预警，并携带建议与证据来源。
     */
    public List<Alert> evaluate(FollowUp current, FollowUp previous, Patient patient, List<AlertRule> rules) {
        List<Alert> alerts = new ArrayList<>();
        if (current == null || rules == null) {
            return alerts;
        }

        for (AlertRule rule : rules) {
            if (!isRuleTriggered(rule, current, previous, patient)) {
                continue;
            }
            Alert alert = buildAlert(current.getPatientId(), rule, current);
            alerts.add(alert);
        }
        return alerts;
    }

    private boolean isRuleTriggered(AlertRule rule, FollowUp current, FollowUp previous, Patient patient) {
        String ruleType = rule.getRuleType() == null
                ? DomainConstants.ALERT_RULE_TYPE_THRESHOLD : rule.getRuleType();
        switch (ruleType) {
            case DomainConstants.ALERT_RULE_TYPE_TREND:
                return previous != null && checkTrend(current, previous, rule);
            case DomainConstants.ALERT_RULE_TYPE_COMORBIDITY:
                return patient != null && checkComorbidity(patient, rule);
            case DomainConstants.ALERT_RULE_TYPE_MEDICATION:
                return checkMedication(current, rule);
            case DomainConstants.ALERT_RULE_TYPE_THRESHOLD:
            default:
                return previous != null
                        && checkIndicator(current, rule)
                        && checkIndicator(previous, rule);
        }
    }

    private boolean checkIndicator(FollowUp followUp, AlertRule rule) {
        return getIndicatorValue(followUp, rule.getIndicator())
                .map(value -> value.compareTo(rule.getThreshold()) >= 0)
                .orElse(false);
    }

    private boolean checkTrend(FollowUp current, FollowUp previous, AlertRule rule) {
        Optional<BigDecimal> currentValue = getIndicatorValue(current, rule.getIndicator());
        Optional<BigDecimal> previousValue = getIndicatorValue(previous, rule.getIndicator());
        if (currentValue.isEmpty() || previousValue.isEmpty()) {
            return false;
        }
        BigDecimal delta = parseConditionNumber(rule.getConditionJson(), "delta")
                .orElse(rule.getThreshold());
        return currentValue.get().subtract(previousValue.get()).compareTo(delta) >= 0;
    }

    private boolean checkComorbidity(Patient patient, AlertRule rule) {
        Optional<JsonNode> node = parseCondition(rule.getConditionJson());
        if (node.isEmpty() || !node.get().has("diseaseTypes")) {
            return false;
        }
        Set<String> expected = new HashSet<>();
        node.get().get("diseaseTypes").forEach(item -> expected.add(item.asText()));
        return expected.contains(patient.getDiseaseType());
    }

    private boolean checkMedication(FollowUp followUp, AlertRule rule) {
        Optional<JsonNode> node = parseCondition(rule.getConditionJson());
        if (node.isEmpty() || !node.get().has("adherenceValues")) {
            return false;
        }
        String adherence = followUp.getMedicationAdherence();
        if (adherence == null) {
            return false;
        }
        for (JsonNode item : node.get().get("adherenceValues")) {
            if (item.asText().equals(adherence)) {
                return true;
            }
        }
        return false;
    }

    private Alert buildAlert(Long patientId, AlertRule rule, FollowUp current) {
        Alert alert = new Alert();
        alert.setPatientId(patientId);
        alert.setAlertType(DomainConstants.ALERT_TYPE_HIGH_RISK);
        alert.setAlertLevel(rule.getAlertLevel());
        alert.setAlertReason(buildReason(rule, current));
        alert.setIsResolved(0);
        alert.setAlertStatus(DomainConstants.ALERT_STATUS_PENDING);
        alert.setRecommendedActions(rule.getRecommendedActions());
        alert.setRecheckItems(rule.getRecheckItems());
        alert.setReferralConditions(rule.getReferralConditions());
        alert.setEvidenceSource(rule.getEvidenceSource());
        alert.setRiskLevel(rule.getRiskLevel());
        return alert;
    }

    private String buildReason(AlertRule rule, FollowUp current) {
        if (DomainConstants.ALERT_RULE_TYPE_THRESHOLD.equals(rule.getRuleType())) {
            String latestValue = getIndicatorValue(current, rule.getIndicator())
                    .map(String::valueOf)
                    .orElse("-");
            return "连续2次" + rule.getRuleName() + "：最近值" + latestValue;
        }
        return rule.getRuleName();
    }

    private Optional<JsonNode> parseCondition(String conditionJson) {
        if (conditionJson == null || conditionJson.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(objectMapper.readTree(conditionJson));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    private Optional<BigDecimal> parseConditionNumber(String conditionJson, String field) {
        return parseCondition(conditionJson)
                .filter(node -> node.has(field))
                .map(node -> node.get(field).decimalValue());
    }

    private Optional<BigDecimal> getIndicatorValue(FollowUp followUp, String indicator) {
        switch (indicator) {
            case "systolic_bp":
                return followUp.getSystolicBp() == null
                        ? Optional.empty()
                        : Optional.of(BigDecimal.valueOf(followUp.getSystolicBp()));
            case "diastolic_bp":
                return followUp.getDiastolicBp() == null
                        ? Optional.empty()
                        : Optional.of(BigDecimal.valueOf(followUp.getDiastolicBp()));
            case "fasting_glucose":
                return Optional.ofNullable(followUp.getFastingGlucose());
            case "postprandial_glucose":
                return Optional.ofNullable(followUp.getPostprandialGlucose());
            default:
                return Optional.empty();
        }
    }
}
