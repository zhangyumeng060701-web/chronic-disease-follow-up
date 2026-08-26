package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.constant.DomainConstants;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUp;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.service.LostFollowUpAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LostFollowUpAlertServiceImpl implements LostFollowUpAlertService {

    private static final long YELLOW_THRESHOLD_DAYS = 7L;
    private static final long RED_THRESHOLD_DAYS = 30L;

    @Autowired
    private FollowUpMapper followUpMapper;
    @Autowired
    private AlertMapper alertMapper;

    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void generateLostFollowUpAlerts() {
        long start = System.currentTimeMillis();
        List<Long> patientIds = followUpMapper.findOverduePatientIds();
        if (patientIds.isEmpty()) {
            log.info("generateLostFollowUpAlerts: no overdue patients, cost={}ms",
                    System.currentTimeMillis() - start);
            return;
        }

        List<FollowUp> latestFollowUps = followUpMapper.selectList(new LambdaQueryWrapper<FollowUp>()
                .in(FollowUp::getPatientId, patientIds)
                .isNotNull(FollowUp::getNextFollowUpDate)
                .orderByAsc(FollowUp::getPatientId)
                .orderByDesc(FollowUp::getFollowUpDate));
        Map<Long, FollowUp> latestByPatient = latestFollowUps.stream()
                .collect(Collectors.toMap(FollowUp::getPatientId, f -> f, (first, second) -> first));

        List<Alert> existingAlerts = alertMapper.selectList(new LambdaQueryWrapper<Alert>()
                .in(Alert::getPatientId, patientIds)
                .eq(Alert::getAlertType, DomainConstants.ALERT_TYPE_LOST_FOLLOW_UP)
                .eq(Alert::getIsResolved, 0));
        Map<Long, Alert> unresolvedByPatient = existingAlerts.stream()
                .collect(Collectors.toMap(Alert::getPatientId, a -> a, (first, second) -> first));

        List<Alert> newAlerts = new ArrayList<>();
        List<Alert> upgradedAlerts = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Map.Entry<Long, FollowUp> entry : latestByPatient.entrySet()) {
            Long patientId = entry.getKey();
            LocalDate nextFollowUpDate = entry.getValue().getNextFollowUpDate();
            if (nextFollowUpDate == null) {
                continue;
            }

            long overdueDays = ChronoUnit.DAYS.between(nextFollowUpDate, today);
            if (overdueDays < YELLOW_THRESHOLD_DAYS) {
                continue;
            }

            String level = overdueDays >= RED_THRESHOLD_DAYS
                    ? DomainConstants.ALERT_LEVEL_RED
                    : DomainConstants.ALERT_LEVEL_YELLOW;
            Alert existing = unresolvedByPatient.get(patientId);
            if (existing == null) {
                newAlerts.add(buildAlert(patientId, level, overdueDays, nextFollowUpDate));
                continue;
            }

            if (DomainConstants.ALERT_LEVEL_RED.equals(level)
                    && !DomainConstants.ALERT_LEVEL_RED.equals(existing.getAlertLevel())) {
                existing.setAlertLevel(DomainConstants.ALERT_LEVEL_RED);
                existing.setAlertReason(buildReason(overdueDays, nextFollowUpDate));
                upgradedAlerts.add(existing);
            }
        }

        if (!newAlerts.isEmpty()) {
            alertMapper.batchInsert(newAlerts);
        }
        upgradedAlerts.forEach(alertMapper::updateById);

        log.info("generateLostFollowUpAlerts: patients={} new={} upgraded={} cost={}ms",
                patientIds.size(), newAlerts.size(), upgradedAlerts.size(),
                System.currentTimeMillis() - start);
    }

    private Alert buildAlert(Long patientId, String level, long overdueDays, LocalDate nextFollowUpDate) {
        Alert alert = new Alert();
        alert.setPatientId(patientId);
        alert.setAlertType(DomainConstants.ALERT_TYPE_LOST_FOLLOW_UP);
        alert.setAlertLevel(level);
        alert.setAlertReason(buildReason(overdueDays, nextFollowUpDate));
        alert.setIsResolved(0);
        return alert;
    }

    private String buildReason(long overdueDays, LocalDate nextFollowUpDate) {
        return String.format("超过下次随访日期 %s 天未随访（下次随访日期：%s）",
                overdueDays, nextFollowUpDate);
    }
}
