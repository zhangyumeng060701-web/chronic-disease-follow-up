/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.constant.DomainConstants;
import com.example.followup.dto.response.DoctorStats;
import com.example.followup.dto.response.StatsOverview;
import com.example.followup.dto.response.TrendItem;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.FollowUpTask;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.FollowUpTaskMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.StatsService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * StatsServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class StatsServiceImpl implements StatsService {
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private FollowUpMapper followUpMapper;
    @Autowired
    private AlertMapper alertMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private FollowUpTaskMapper followUpTaskMapper;

/**
 * 执行 getOverview 操作。
 */
    @Override
    public StatsOverview getOverview() {
        long start = System.currentTimeMillis();
        boolean admin = SecurityUtils.isAdmin();
        Long currentDoctorId = admin ? null : SecurityUtils.currentUser().getUserId();

        Long totalPatients = patientMapper.selectCount(activePatientWrapper(currentDoctorId));
        List<Long> activePatientIds = activePatientIds(currentDoctorId);
        Integer monthlyCompleted = countMonthlyFollowedPatients(activePatientIds);
        Integer monthlyExpected = totalPatients != null ? totalPatients.intValue() : 0;

        String completionRate = "-";
        if (monthlyCompleted != null && monthlyExpected > 0) {
            BigDecimal rate = BigDecimal.valueOf(monthlyCompleted)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(monthlyExpected), 1, RoundingMode.HALF_UP);
            completionRate = rate + "%";
        }

        Long highRiskCount = countDistinctAlerts(null, DomainConstants.ALERT_LEVEL_RED, activePatientIds);
        Long lostFollowUpCount = countDistinctAlerts(
                DomainConstants.ALERT_TYPE_LOST_FOLLOW_UP, null, activePatientIds);

        String planCompletionRate = formatRate(countCompletedTasks(currentDoctorId), countTotalTasks(currentDoctorId));
        String followUpTaskCompletionRate = formatRate(
                countMonthCompletedTasks(currentDoctorId), countMonthTasks(currentDoctorId));
        String avgAlertResponseHours = calculateAvgAlertResponseHours(activePatientIds);

        StatsOverview result = new StatsOverview(
                totalPatients != null ? totalPatients : 0,
                monthlyCompleted != null ? monthlyCompleted.intValue() : 0,
                monthlyExpected,
                completionRate,
                highRiskCount != null ? highRiskCount : 0,
                lostFollowUpCount != null ? lostFollowUpCount : 0,
                planCompletionRate,
                followUpTaskCompletionRate,
                avgAlertResponseHours
        );
        log.info("getOverview admin={} cost={}ms", admin, System.currentTimeMillis() - start);
        return result;
    }

    private LambdaQueryWrapper<Patient> activePatientWrapper(Long doctorId) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getStatus, 1);
        if (doctorId != null) {
            wrapper.eq(Patient::getDoctorId, doctorId);
        }
        return wrapper;
    }

    private List<Long> activePatientIds(Long doctorId) {
        return patientMapper.selectList(activePatientWrapper(doctorId).select(Patient::getId))
                .stream()
                .map(Patient::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private Integer countMonthlyFollowedPatients(List<Long> activePatientIds) {
        if (activePatientIds.isEmpty()) {
            return 0;
        }
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(FollowUp::getPatientId)
                .in(FollowUp::getPatientId, activePatientIds)
                .ge(FollowUp::getFollowUpDate, LocalDate.now().withDayOfMonth(1));
        long count = followUpMapper.selectList(wrapper).stream()
                .map(FollowUp::getPatientId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        return Math.toIntExact(count);
    }

    private Long countTotalTasks(Long doctorId) {
        LambdaQueryWrapper<FollowUpTask> wrapper = new LambdaQueryWrapper<>();
        if (doctorId != null) {
            wrapper.eq(FollowUpTask::getOwnerId, doctorId);
        }
        return followUpTaskMapper.selectCount(wrapper);
    }

    private Long countCompletedTasks(Long doctorId) {
        LambdaQueryWrapper<FollowUpTask> wrapper = new LambdaQueryWrapper<FollowUpTask>()
                .eq(FollowUpTask::getStatus, DomainConstants.TASK_STATUS_COMPLETED);
        if (doctorId != null) {
            wrapper.eq(FollowUpTask::getOwnerId, doctorId);
        }
        return followUpTaskMapper.selectCount(wrapper);
    }

    private Long countMonthTasks(Long doctorId) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        LambdaQueryWrapper<FollowUpTask> wrapper = new LambdaQueryWrapper<FollowUpTask>()
                .between(FollowUpTask::getDueDate, monthStart, monthEnd);
        if (doctorId != null) {
            wrapper.eq(FollowUpTask::getOwnerId, doctorId);
        }
        return followUpTaskMapper.selectCount(wrapper);
    }

    private Long countMonthCompletedTasks(Long doctorId) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        LambdaQueryWrapper<FollowUpTask> wrapper = new LambdaQueryWrapper<FollowUpTask>()
                .between(FollowUpTask::getDueDate, monthStart, monthEnd)
                .eq(FollowUpTask::getStatus, DomainConstants.TASK_STATUS_COMPLETED);
        if (doctorId != null) {
            wrapper.eq(FollowUpTask::getOwnerId, doctorId);
        }
        return followUpTaskMapper.selectCount(wrapper);
    }

    private String formatRate(long completed, long total) {
        if (total <= 0) {
            return "-";
        }
        BigDecimal rate = BigDecimal.valueOf(completed)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
        return rate + "%";
    }

    private String calculateAvgAlertResponseHours(List<Long> activePatientIds) {
        if (activePatientIds.isEmpty()) {
            return "-";
        }
        List<Alert> resolvedAlerts = alertMapper.selectList(new LambdaQueryWrapper<Alert>()
                .in(Alert::getPatientId, activePatientIds)
                .in(Alert::getAlertStatus,
                        DomainConstants.ALERT_STATUS_RESOLVED,
                        DomainConstants.ALERT_STATUS_REFERRED)
                .isNotNull(Alert::getResolveTime));
        if (resolvedAlerts.isEmpty()) {
            return "-";
        }
        long totalMinutes = 0;
        long count = 0;
        for (Alert alert : resolvedAlerts) {
            if (alert.getCreateTime() == null) {
                continue;
            }
            long minutes = ChronoUnit.MINUTES.between(alert.getCreateTime(), alert.getResolveTime());
            if (minutes >= 0) {
                totalMinutes += minutes;
                count++;
            }
        }
        if (count == 0) {
            return "-";
        }
        BigDecimal avgHours = BigDecimal.valueOf(totalMinutes)
                .divide(BigDecimal.valueOf(60L * count), 1, RoundingMode.HALF_UP);
        return avgHours + "小时";
    }

/**
 * 执行 getBpTrend 操作。
 */
    @Override
    public List<TrendItem> getBpTrend() {
        return getTrend("bp");
    }

    @Override
    public List<TrendItem> getGlucoseTrend() {
        return getTrend("glucose");
    }

    private List<TrendItem> getTrend(String type) {
        boolean admin = SecurityUtils.isAdmin();
        Long currentDoctorId = admin ? null : SecurityUtils.currentUser().getUserId();
        List<Long> activePatientIds = activePatientIds(currentDoctorId);
        List<TrendItem> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        if (activePatientIds.isEmpty()) {
            for (int i = 11; i >= 0; i--) {
                LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
                result.add(new TrendItem(monthStart.format(fmt), 0.0));
            }
            return result;
        }

        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(FollowUp::getFollowUpDate, monthStart, monthEnd)
                   .in(FollowUp::getPatientId, activePatientIds)
                   .orderByDesc(FollowUp::getFollowUpDate);
            List<FollowUp> records = followUpMapper.selectList(wrapper);

            Map<Long, FollowUp> latestMap = records.stream()
                    .filter(f -> f.getPatientId() != null)
                    .collect(Collectors.toMap(FollowUp::getPatientId, f -> f, (a, b) -> a));

            long total = latestMap.size();
            long controlled = 0;

            if ("bp".equals(type)) {
                controlled = latestMap.values().stream()
                        .filter(f -> f.getSystolicBp() != null && f.getSystolicBp() < 140
                                  && f.getDiastolicBp() != null && f.getDiastolicBp() < 90)
                        .count();
            } else {
                controlled = latestMap.values().stream()
                        .filter(f -> f.getFastingGlucose() != null
                                  && f.getFastingGlucose().compareTo(new BigDecimal("7.0")) < 0)
                        .count();
            }

            double rate = total > 0
                    ? BigDecimal.valueOf(controlled)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP)
                            .doubleValue()
                    : 0.0;
            result.add(new TrendItem(monthStart.format(fmt), rate));
        }
        return result;
    }

/**
 * 执行 getDoctorComparison 操作。
 */
    @Override
    public List<DoctorStats> getDoctorComparison() {
        long start = System.currentTimeMillis();
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getRole, DomainConstants.ROLE_DOCTOR).eq(SysUser::getStatus, 1);
        if (!SecurityUtils.isAdmin()) {
            userWrapper.eq(SysUser::getId, SecurityUtils.currentUser().getUserId());
        }
        List<SysUser> doctors = sysUserMapper.selectList(userWrapper);
        if (doctors.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> doctorIds = doctors.stream().map(SysUser::getId).collect(Collectors.toList());

        List<Patient> patients = patientMapper.selectList(
                new LambdaQueryWrapper<Patient>()
                        .in(Patient::getDoctorId, doctorIds)
                        .eq(Patient::getStatus, 1)
        );
        if (patients.isEmpty()) {
            return doctors.stream()
                    .map(doc -> new DoctorStats(doc.getId(), doc.getRealName(), 0L, "-", 0L))
                    .collect(Collectors.toList());
        }

        // 一次查询患者、当月随访和高危预警，再按医生分组，避免循环查询造成 N+1
        Map<Long, Long> patientCountByDoctor = patients.stream()
                .collect(Collectors.groupingBy(Patient::getDoctorId, Collectors.counting()));

        Map<Long, Long> patientDoctorMap = patients.stream()
                .collect(Collectors.toMap(Patient::getId, Patient::getDoctorId, (a, b) -> a));

        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        List<FollowUp> monthlyFollowUps = followUpMapper.selectList(
                new LambdaQueryWrapper<FollowUp>()
                        .in(FollowUp::getPatientId, patientDoctorMap.keySet())
                        .ge(FollowUp::getFollowUpDate, monthStart)
        );
        Map<Long, Long> completedByDoctor = monthlyFollowUps.stream()
                .map(FollowUp::getPatientId)
                .filter(patientDoctorMap::containsKey)
                .distinct()
                .collect(Collectors.toMap(
                        patientDoctorMap::get,
                        patientId -> 1L,
                        Long::sum
                ));

        List<Alert> highRiskAlerts = alertMapper.selectList(
                new LambdaQueryWrapper<Alert>()
                        .in(Alert::getPatientId, patientDoctorMap.keySet())
                        .eq(Alert::getIsResolved, 0)
                        .eq(Alert::getAlertLevel, DomainConstants.ALERT_LEVEL_RED)
        );
        Map<Long, Long> highRiskByDoctor = highRiskAlerts.stream()
                .map(Alert::getPatientId)
                .filter(patientDoctorMap::containsKey)
                .distinct()
                .collect(Collectors.toMap(
                        patientDoctorMap::get,
                        patientId -> 1L,
                        Long::sum
                ));

        List<DoctorStats> result = doctors.stream().map(doc -> {
            long totalWithPlan = patientCountByDoctor.getOrDefault(doc.getId(), 0L);
            long completed = Math.min(completedByDoctor.getOrDefault(doc.getId(), 0L), totalWithPlan);
            String rate = totalWithPlan > 0
                    ? BigDecimal.valueOf(completed)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(totalWithPlan), 1, RoundingMode.HALF_UP)
                            + "%"
                    : "-";
            long highRisk = Math.min(highRiskByDoctor.getOrDefault(doc.getId(), 0L), totalWithPlan);
            return new DoctorStats(doc.getId(), doc.getRealName(), totalWithPlan, rate, highRisk);
        }).collect(Collectors.toList());
        log.info("getDoctorComparison size={} cost={}ms", result.size(), System.currentTimeMillis() - start);
        return result;
    }

    private Long countDistinctAlerts(String alertType, String alertLevel, List<Long> activePatientIds) {
        if (activePatientIds.isEmpty()) {
            return 0L;
        }
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Alert::getPatientId, activePatientIds)
               .eq(Alert::getIsResolved, 0);
        if (alertType != null) {
            wrapper.eq(Alert::getAlertType, alertType);
        }
        if (alertLevel != null) {
            wrapper.eq(Alert::getAlertLevel, alertLevel);
        }
        return alertMapper.selectList(wrapper).stream()
                .map(Alert::getPatientId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }
}
