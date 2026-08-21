package com.example.followup.engine;

import com.example.followup.constant.DomainConstants;
import com.example.followup.entity.Alert;
import com.example.followup.entity.AlertRule;
import com.example.followup.entity.FollowUp;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class AlertRuleEngine {

    /**
     * 连续两次随访命中同一规则时生成高危预警。
     */
    public List<Alert> evaluate(FollowUp current, FollowUp previous, List<AlertRule> rules) {
        List<Alert> alerts = new ArrayList<>();
        if (current == null || previous == null || rules == null) {
            return alerts;
        }

        for (AlertRule rule : rules) {
            boolean currentTriggered = checkIndicator(current, rule);
            boolean previousTriggered = checkIndicator(previous, rule);
            if (currentTriggered && previousTriggered) {
                Alert alert = new Alert();
                alert.setPatientId(current.getPatientId());
                alert.setAlertType(DomainConstants.ALERT_TYPE_HIGH_RISK);
                alert.setAlertLevel(rule.getAlertLevel());
                alert.setAlertReason("连续2次" + rule.getRuleName() + "：最近值" + getIndicatorValue(current, rule.getIndicator()));
                alert.setIsResolved(0);
                alerts.add(alert);
            }
        }
        return alerts;
    }

    private boolean checkIndicator(FollowUp followUp, AlertRule rule) {
        BigDecimal value = getIndicatorValue(followUp, rule.getIndicator());
        return value != null && value.compareTo(rule.getThreshold()) >= 0;
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
