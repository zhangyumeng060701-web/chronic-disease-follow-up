package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.dto.response.DoctorStats;
import com.example.followup.dto.response.StatsOverview;
import com.example.followup.dto.response.TrendItem;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;
import com.example.followup.mapper.*;
import com.example.followup.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public StatsOverview getOverview() {
        LambdaQueryWrapper<Patient> patientWrapper = new LambdaQueryWrapper<>();
        patientWrapper.eq(Patient::getStatus, 1);
        Long totalPatients = patientMapper.selectCount(patientWrapper);

        Integer monthlyCompleted = followUpMapper.countMonthlyCompleted();

        LambdaQueryWrapper<Patient> patientWithNext = new LambdaQueryWrapper<>();
        patientWithNext.eq(Patient::getStatus, 1).isNotNull(Patient::getId);
        Long totalWithFollowUp = patientMapper.selectCount(patientWithNext);
        Integer monthlyExpected = totalWithFollowUp != null ? totalWithFollowUp.intValue() : 0;

        String completionRate = "-";
        if (monthlyCompleted != null && monthlyExpected > 0) {
            BigDecimal rate = BigDecimal.valueOf(monthlyCompleted)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(monthlyExpected), 1, RoundingMode.HALF_UP);
            completionRate = rate + "%";
        }

        Long highRiskCount = alertMapper.countHighRisk();
        Long lostFollowUpCount = alertMapper.countLostFollowUp();

        return new StatsOverview(totalPatients != null ? totalPatients : 0,
                monthlyCompleted != null ? monthlyCompleted : 0, monthlyExpected,
                completionRate, highRiskCount != null ? highRiskCount : 0,
                lostFollowUpCount != null ? lostFollowUpCount : 0);
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
        List<TrendItem> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(FollowUp::getFollowUpDate, monthStart, monthEnd)
                   .orderByDesc(FollowUp::getFollowUpDate);
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
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getRole, "DOCTOR").eq(SysUser::getStatus, 1);
        List<SysUser> doctors = sysUserMapper.selectList(userWrapper);
        List<DoctorStats> result = new ArrayList<>();

        for (SysUser doc : doctors) {
            LambdaQueryWrapper<Patient> pWrapper = new LambdaQueryWrapper<>();
            pWrapper.eq(Patient::getDoctorId, doc.getId()).eq(Patient::getStatus, 1);
            Long patientCount = patientMapper.selectCount(pWrapper);

            List<Patient> patients = patientMapper.selectList(pWrapper);
            long completed = 0;
            long totalWithPlan = 0;
            for (Patient p : patients) {
                LambdaQueryWrapper<FollowUp> fWrapper = new LambdaQueryWrapper<>();
                LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
                fWrapper.eq(FollowUp::getPatientId, p.getId())
                       .ge(FollowUp::getFollowUpDate, monthStart);
                if (followUpMapper.selectCount(fWrapper) > 0) completed++;
                totalWithPlan++;
            }

            String rate = totalWithPlan > 0
                    ? Math.round(completed * 10000.0 / totalWithPlan) / 100.0 + "%" : "-";

            LambdaQueryWrapper<com.example.followup.entity.Alert> aWrapper = new LambdaQueryWrapper<>();
            aWrapper.eq(com.example.followup.entity.Alert::getIsResolved, 0)
                    .eq(com.example.followup.entity.Alert::getAlertLevel, "RED");
            Long highRisk = alertMapper.selectCount(aWrapper);

            result.add(new DoctorStats(doc.getId(), doc.getRealName(),
                    patientCount != null ? patientCount : 0, rate,
                    highRisk != null ? highRisk : 0));
        }
        return result;
    }
}
