package com.example.followup.engine;

import com.example.followup.constant.DomainConstants;
import com.example.followup.entity.Alert;
import com.example.followup.entity.AlertRule;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            if (!isRuleTriggered(rule, current, previous, patient)) continue;
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
        BigDecimal value = getIndicatorValue(followUp, rule.getIndicator());
        return value != null && value.compareTo(rule.getThreshold()) >= 0;
    }

    private boolean checkTrend(FollowUp current, FollowUp previous, AlertRule rule) {
        BigDecimal currentValue = getIndicatorValue(current, rule.getIndicator());
        BigDecimal previousValue = getIndicatorValue(previous, rule.getIndicator());
        if (currentValue == null || previousValue == null) return false;
        BigDecimal delta = parseConditionNumber(rule.getConditionJson(), "delta");
        if (delta == null) delta = rule.getThreshold();
        return currentValue.subtract(previousValue).compareTo(delta) >= 0;
    }

    private boolean checkComorbidity(Patient patient, AlertRule rule) {
        JsonNode node = parseCondition(rule.getConditionJson());
        if (node == null || !node.has("diseaseTypes")) return false;
        Set<String> expected = new HashSet<>();
        node.get("diseaseTypes").forEach(item -> expected.add(item.asText()));
        return expected.contains(patient.getDiseaseType());
    }

    private boolean checkMedication(FollowUp followUp, AlertRule rule) {
        JsonNode node = parseCondition(rule.getConditionJson());
        if (node == null || !node.has("adherenceValues")) return false;
        String adherence = followUp.getMedicationAdherence();
        if (adherence == null) return false;
        for (JsonNode item : node.get("adherenceValues")) {
            if (item.asText().equals(adherence)) return true;
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
            return "连续2次" + rule.getRuleName() + "：最近值" + getIndicatorValue(current, rule.getIndicator());
        }
        return rule.getRuleName();
    }

    private JsonNode parseCondition(String conditionJson) {
        if (conditionJson == null || conditionJson.isBlank()) return null;
        try {
            return objectMapper.readTree(conditionJson);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseConditionNumber(String conditionJson, String field) {
        JsonNode node = parseCondition(conditionJson);
        if (node != null && node.has(field)) {
            return node.get(field).decimalValue();
        }
        return null;
    }

    private BigDecimal getIndicatorValue(FollowUp followUp, String indicator) {
        switch (indicator) {
            case "systolic_bp":
                return followUp.getSystolicBp() != null ? BigDecimal.valueOf(followUp.getSystolicBp()) : null;
            case "diastolic_bp":
                return followUp.getDiastolicBp() != null ? BigDecimal.valueOf(followUp.getDiastolicBp()) : null;
            case "fasting_glucose":
                return followUp.getFastingGlucose();
            case "postprandial_glucose":
                return followUp.getPostprandialGlucose();
            default:
                return null;
        }
    }
}
