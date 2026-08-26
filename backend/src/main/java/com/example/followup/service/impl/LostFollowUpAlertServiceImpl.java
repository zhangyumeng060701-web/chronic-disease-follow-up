package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.constant.DomainConstants;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.LostFollowUpAlertRecord;
import com.example.followup.mapper.LostFollowUpQueryMapper;
import com.example.followup.service.LostFollowUpAlertService;
import com.example.followup.service.LostFollowUpScanResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LostFollowUpAlertServiceImpl implements LostFollowUpAlertService {
    static final int YELLOW_THRESHOLD_DAYS = 7;
    static final int RED_THRESHOLD_DAYS = 30;

    private final LostFollowUpQueryMapper mapper;
    private final Clock businessClock;

    @Override
    @Transactional
    public LostFollowUpScanResult scanAndGenerateAlerts() {
        LocalDate today = LocalDate.now(businessClock);
        List<FollowUp> candidates = mapper.findLatestDueFollowUps(today.minusDays(YELLOW_THRESHOLD_DAYS));
        int yellowCreated = 0;
        int redCreated = 0;
        int yellowResolved = 0;
        int skipped = 0;

        for (FollowUp followUp : candidates) {
            long overdueDays = ChronoUnit.DAYS.between(followUp.getNextFollowUpDate(), today);
            if (overdueDays >= RED_THRESHOLD_DAYS) {
                yellowResolved += resolveLevel(followUp.getPatientId(), followUp.getNextFollowUpDate(), "YELLOW");
                if (createIfAbsent(followUp, "RED", overdueDays)) {
                    redCreated++;
                } else {
                    skipped++;
                }
            } else if (createIfAbsent(followUp, "YELLOW", overdueDays)) {
                yellowCreated++;
            } else {
                skipped++;
            }
        }

        LostFollowUpScanResult result = new LostFollowUpScanResult(
                candidates.size(), yellowCreated, redCreated, yellowResolved, skipped);
        log.info("lost-follow-up scan scanned={} yellowCreated={} redCreated={} yellowResolved={} skipped={}",
                result.getScannedCount(), result.getYellowCreated(), result.getRedCreated(),
                result.getYellowResolved(), result.getSkippedCount());
        return result;
    }

    @Override
    @Transactional
    public int resolveOutstandingAlerts(Long patientId) {
        List<LostFollowUpAlertRecord> alerts = mapper.selectList(new LambdaQueryWrapper<LostFollowUpAlertRecord>()
                .eq(LostFollowUpAlertRecord::getPatientId, patientId)
                .eq(LostFollowUpAlertRecord::getAlertType, DomainConstants.ALERT_TYPE_LOST_FOLLOW_UP)
                .eq(LostFollowUpAlertRecord::getIsResolved, 0));
        alerts.forEach(this::resolve);
        return alerts.size();
    }

    private boolean createIfAbsent(FollowUp followUp, String level, long overdueDays) {
        if (mapper.selectCount(cycleQuery(followUp.getPatientId(), followUp.getNextFollowUpDate(), level)) > 0) {
            return false;
        }
        LostFollowUpAlertRecord alert = new LostFollowUpAlertRecord();
        alert.setPatientId(followUp.getPatientId());
        alert.setAlertType(DomainConstants.ALERT_TYPE_LOST_FOLLOW_UP);
        alert.setAlertLevel(level);
        alert.setAlertReason("超过下次随访日期" + overdueDays + "天仍未随访");
        alert.setSourceDueDate(followUp.getNextFollowUpDate());
        alert.setIsResolved(0);
        try {
            return mapper.insert(alert) == 1;
        } catch (DuplicateKeyException ignored) {
            log.debug("duplicate lost-follow-up alert patientId={} level={} dueDate={}",
                    followUp.getPatientId(), level, followUp.getNextFollowUpDate());
            return false;
        }
    }

    private int resolveLevel(Long patientId, LocalDate dueDate, String level) {
        List<LostFollowUpAlertRecord> alerts = mapper.selectList(cycleQuery(patientId, dueDate, level)
                .eq(LostFollowUpAlertRecord::getIsResolved, 0));
        alerts.forEach(this::resolve);
        return alerts.size();
    }

    private LambdaQueryWrapper<LostFollowUpAlertRecord> cycleQuery(Long patientId, LocalDate dueDate, String level) {
        return new LambdaQueryWrapper<LostFollowUpAlertRecord>()
                .eq(LostFollowUpAlertRecord::getPatientId, patientId)
                .eq(LostFollowUpAlertRecord::getAlertType, DomainConstants.ALERT_TYPE_LOST_FOLLOW_UP)
                .eq(LostFollowUpAlertRecord::getAlertLevel, level)
                .eq(LostFollowUpAlertRecord::getSourceDueDate, dueDate);
    }

    private void resolve(LostFollowUpAlertRecord alert) {
        alert.setIsResolved(1);
        alert.setResolveTime(LocalDateTime.now(businessClock));
        mapper.updateById(alert);
    }
}
