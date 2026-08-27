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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public StatsOverview getOverview() {
        long start = System.currentTimeMillis();
        boolean admin = SecurityUtils.isAdmin();
        Long currentDoctorId = admin ? null : SecurityUtils.currentUser().getUserId();

        LambdaQueryWrapper<Patient> patientWrapper = new LambdaQueryWrapper<>();
        patientWrapper.eq(Patient::getStatus, 1);
        if (!admin) {
            patientWrapper.eq(Patient::getDoctorId, currentDoctorId);
        }
        Long totalPatients = patientMapper.selectCount(patientWrapper);

        LambdaQueryWrapper<FollowUp> monthlyWrapper = new LambdaQueryWrapper<>();
        monthlyWrapper.ge(FollowUp::getFollowUpDate, LocalDate.now().withDayOfMonth(1));
        if (!admin) {
            monthlyWrapper.eq(FollowUp::getDoctorId, currentDoctorId);
        }
        Long monthlyCompleted = followUpMapper.selectCount(monthlyWrapper);

        LambdaQueryWrapper<Patient> patientWithNext = new LambdaQueryWrapper<>();
        patientWithNext.eq(Patient::getStatus, 1).isNotNull(Patient::getId);
        if (!admin) {
            patientWithNext.eq(Patient::getDoctorId, currentDoctorId);
        }
        Long totalWithFollowUp = patientMapper.selectCount(patientWithNext);
        Integer monthlyExpected = totalWithFollowUp != null ? totalWithFollowUp.intValue() : 0;

        String completionRate = "-";
        if (monthlyCompleted != null && monthlyExpected > 0) {
            BigDecimal rate = BigDecimal.valueOf(monthlyCompleted)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(monthlyExpected), 1, RoundingMode.HALF_UP);
            completionRate = rate + "%";
        }

        Long highRiskCount = admin
                ? countDistinctAlerts(null, DomainConstants.ALERT_LEVEL_RED)
                : countAlertsForDoctor(currentDoctorId, null, DomainConstants.ALERT_LEVEL_RED);
        Long lostFollowUpCount = admin
                ? countDistinctAlerts(DomainConstants.ALERT_TYPE_LOST_FOLLOW_UP, null)
                : countAlertsForDoctor(currentDoctorId, DomainConstants.ALERT_TYPE_LOST_FOLLOW_UP, null);

        String planCompletionRate = formatRate(countCompletedTasks(), countTotalTasks());
        String followUpTaskCompletionRate = formatRate(countMonthCompletedTasks(), countMonthTasks());
        String avgAlertResponseHours = calculateAvgAlertResponseHours();

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

    private Long countTotalTasks() {
        return followUpTaskMapper.selectCount(new LambdaQueryWrapper<>());
    }

    private Long countCompletedTasks() {
        return followUpTaskMapper.selectCount(new LambdaQueryWrapper<FollowUpTask>()
                .eq(FollowUpTask::getStatus, DomainConstants.TASK_STATUS_COMPLETED));
    }

    private Long countMonthTasks() {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        return followUpTaskMapper.selectCount(new LambdaQueryWrapper<FollowUpTask>()
                .between(FollowUpTask::getDueDate, monthStart, monthEnd));
    }

    private Long countMonthCompletedTasks() {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        return followUpTaskMapper.selectCount(new LambdaQueryWrapper<FollowUpTask>()
                .between(FollowUpTask::getDueDate, monthStart, monthEnd)
                .eq(FollowUpTask::getStatus, DomainConstants.TASK_STATUS_COMPLETED));
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

    private String calculateAvgAlertResponseHours() {
        List<Alert> resolvedAlerts = alertMapper.selectList(new LambdaQueryWrapper<Alert>()
                .in(Alert::getAlertStatus,
                        DomainConstants.ALERT_STATUS_RESOLVED,
                        DomainConstants.ALERT_STATUS_REFERRED)
                .isNotNull(Alert::getResolveTime));
        if (resolvedAlerts.isEmpty()) {
            return "-";
        }
        long totalMinutes = resolvedAlerts.stream()
                .filter(a -> a.getCreateTime() != null)
                .mapToLong(a -> ChronoUnit.MINUTES.between(a.getCreateTime(), a.getResolveTime()))
                .filter(minutes -> minutes >= 0)
                .sum();
        long count = resolvedAlerts.stream()
                .filter(a -> a.getCreateTime() != null)
                .filter(a -> ChronoUnit.MINUTES.between(a.getCreateTime(), a.getResolveTime()) >= 0)
                .count();
        if (count == 0) {
            return "-";
        }
        return String.format("%.1f小时", totalMinutes / 60.0 / count);
    }

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
        List<TrendItem> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(FollowUp::getFollowUpDate, monthStart, monthEnd)
                   .orderByDesc(FollowUp::getFollowUpDate);
            if (!admin) {
                wrapper.eq(FollowUp::getDoctorId, currentDoctorId);
            }
            List<FollowUp> records = followUpMapper.selectList(wrapper);

            Map<Long, FollowUp> latestMap = records.stream()
                    .collect(Collectors.toMap(FollowUp::getPatientId, f -> f, (a, b) -> a));

            long total = latestMap.size();
            long controlled = 0;

            if (type.equals("bp")) {
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

            double rate = total > 0 ? Math.round(controlled * 10000.0 / total) / 100.0 : 0;
            result.add(new TrendItem(monthStart.format(fmt), rate));
        }
        return result;
    }

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
            long completed = completedByDoctor.getOrDefault(doc.getId(), 0L);
            String rate = totalWithPlan > 0
                    ? Math.round(completed * 10000.0 / totalWithPlan) / 100.0 + "%" : "-";
            long highRisk = highRiskByDoctor.getOrDefault(doc.getId(), 0L);
            return new DoctorStats(doc.getId(), doc.getRealName(), totalWithPlan, rate, highRisk);
        }).collect(Collectors.toList());
        log.info("getDoctorComparison size={} cost={}ms", result.size(), System.currentTimeMillis() - start);
        return result;
    }

    private Long countAlertsForDoctor(Long doctorId, String alertType, String alertLevel) {
        List<Patient> patients = patientMapper.selectList(
                new LambdaQueryWrapper<Patient>()
                        .eq(Patient::getDoctorId, doctorId)
                        .eq(Patient::getStatus, 1)
        );
        if (patients.isEmpty()) {
            return 0L;
        }
        List<Long> patientIds = patients.stream().map(Patient::getId).collect(Collectors.toList());
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Alert::getPatientId, patientIds)
               .eq(Alert::getIsResolved, 0);
        if (alertType != null) {
            wrapper.eq(Alert::getAlertType, alertType);
        }
        if (alertLevel != null) {
            wrapper.eq(Alert::getAlertLevel, alertLevel);
        }
        return alertMapper.selectList(wrapper).stream()
                .map(Alert::getPatientId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();
    }

    private Long countDistinctAlerts(String alertType, String alertLevel) {
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Alert::getIsResolved, 0);
        if (alertType != null) {
            wrapper.eq(Alert::getAlertType, alertType);
        }
        if (alertLevel != null) {
            wrapper.eq(Alert::getAlertLevel, alertLevel);
        }
        return alertMapper.selectList(wrapper).stream()
                .map(Alert::getPatientId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();
    }
}
