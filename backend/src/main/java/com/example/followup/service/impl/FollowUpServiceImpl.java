/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.FollowUpQuery;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.engine.AlertRuleEngine;
import com.example.followup.entity.Alert;
import com.example.followup.entity.AlertRule;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.AlertRuleMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.FollowUpService;
import com.example.followup.util.DesensitizationUtil;
import com.example.followup.util.VoMappers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.stream.Collectors;

/**
 * FollowUpServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class FollowUpServiceImpl implements FollowUpService {
    @Autowired
    private FollowUpMapper followUpMapper;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private AlertRuleMapper alertRuleMapper;
    @Autowired
    private AlertMapper alertMapper;
    @Autowired
    private AlertRuleEngine alertRuleEngine;

    /**
     * 查询listFollowUps。
     *
     * @param query 参数说明
     * @return 返回值
     */
    @Override
    public PageResponse<FollowUpVO> listFollowUps(FollowUpQuery query) {
        if (query.getStartDate() != null && query.getEndDate() != null
                && query.getStartDate().isAfter(query.getEndDate())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "开始日期不能晚于结束日期");
        }
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        boolean isAdmin = SecurityUtils.isAdmin();
        if (!isAdmin) {
            wrapper.eq(FollowUp::getDoctorId, SecurityUtils.currentUser().getUserId());
        }
        if (query.getPatientId() != null) {
            wrapper.eq(FollowUp::getPatientId, query.getPatientId());
        }
        if (query.getStartDate() != null) {
            wrapper.ge(FollowUp::getFollowUpDate, query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le(FollowUp::getFollowUpDate, query.getEndDate());
        }
        if (query.getNextFollowUpDateBefore() != null) {
            wrapper.le(FollowUp::getNextFollowUpDate, query.getNextFollowUpDateBefore());
        }
        wrapper.orderByDesc(FollowUp::getFollowUpDate);

        Page<FollowUp> page = new Page<>(query.getPage(), query.getSize());
        followUpMapper.selectPage(page, wrapper);

        List<Long> patientIds = page.getRecords().stream()
                .map(FollowUp::getPatientId).distinct().collect(Collectors.toList());
        Map<Long, String> nameMap = patientIds.isEmpty()
                ? Map.of()
                : patientMapper.selectBatchIds(patientIds).stream()
                        .collect(Collectors.toMap(Patient::getId, Patient::getName));

        List<FollowUpVO> vos = page.getRecords().stream().map(f -> {
            FollowUpVO vo = VoMappers.toFollowUpVO(f);
            String patientName = nameMap.getOrDefault(f.getPatientId(), "");
            vo.setPatientName(isAdmin ? patientName : DesensitizationUtil.maskName(patientName));
            return vo;
        }).collect(Collectors.toList());

        long start = System.currentTimeMillis();
        log.info("listFollowUps userId={} total={} cost={}ms",
                currentUserIdSafely().orElse(-1L), page.getTotal(), System.currentTimeMillis() - start);
        return PageResponseUtil.of(page, vos, query.getPage(), query.getSize());
    }

    /**
     * 查询getFollowUpById。
     *
     * @param id 参数说明
     * @return 返回值
     */
    @Override
    public FollowUp getFollowUpById(Long id) {
        FollowUp followUp = followUpMapper.selectById(id);
        if (followUp == null) {
            throw new BusinessException(ErrorCode.FOLLOWUP_NOT_FOUND);
        }
        if (!SecurityUtils.isAdmin()
                && !Objects.equals(followUp.getDoctorId(), SecurityUtils.currentUser().getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        long start = System.currentTimeMillis();
        log.info("getFollowUpById id={} cost={}ms", id, System.currentTimeMillis() - start);
        return followUp;
    }

    /**
     * 新增addFollowUp。
     *
     * @param followUp 参数说明
     */
    @Override
    @Transactional
    public void addFollowUp(FollowUp followUp) {
        followUp.setId(null);
        followUpMapper.insert(followUp);
        checkAndGenerateAlerts(followUp);
        long start = System.currentTimeMillis();
        log.info("addFollowUp id={} cost={}ms", followUp.getId(), System.currentTimeMillis() - start);
    }

    /**
     * 更新updateFollowUp。
     *
     * @param followUp 参数说明
     */
    @Override
    @Transactional
    public void updateFollowUp(FollowUp followUp) {
        getFollowUpById(followUp.getId());
        followUpMapper.updateById(followUp);
        long start = System.currentTimeMillis();
        log.info("updateFollowUp id={} cost={}ms", followUp.getId(), System.currentTimeMillis() - start);
    }

    /**
     * 删除deleteFollowUp。
     *
     * @param id 参数说明
     */
    @Override
    @Transactional
    public void deleteFollowUp(Long id) {
        getFollowUpById(id);
        followUpMapper.deleteById(id);
        long start = System.currentTimeMillis();
        log.info("deleteFollowUp id={} cost={}ms", id, System.currentTimeMillis() - start);
    }

    /**
     * 查询listOverdueFollowUps。
     *
     * @return 返回值
     */
    @Override
    public List<FollowUpVO> listOverdueFollowUps() {
        FollowUpQuery query = new FollowUpQuery();
        query.setPage(1);
        query.setSize(Integer.MAX_VALUE);
        // 超期 7 天以上：下次随访日期早于 7 天前
        query.setNextFollowUpDateBefore(LocalDate.now().minusDays(7));
        return listFollowUps(query).getRecords();
    }

    // ---- 连续异常预警 ----

    private void checkAndGenerateAlerts(FollowUp followUp) {
        Long patientId = followUp.getPatientId();

        // 只对比当前记录之前最近一次随访，避免连续异常被重复计算
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowUp::getPatientId, patientId)
            .lt(FollowUp::getId, followUp.getId())
            .orderByDesc(FollowUp::getFollowUpDate)
            .last("LIMIT 1");
        FollowUp previous = followUpMapper.selectList(wrapper).stream().findFirst().orElse(null);
        if (previous == null) {
            return;
        }

        List<AlertRule> rules = alertRuleMapper.findActiveRules();
        Patient patient = patientMapper.selectById(patientId);
        // 规则匹配统一交给 AlertRuleEngine，Service 只负责数据获取与落库
        List<Alert> alerts = alertRuleEngine.evaluate(followUp, previous, patient, rules);

        if (!alerts.isEmpty()) {
            alertMapper.batchInsert(alerts);
        }
    }

    private OptionalLong currentUserIdSafely() {
        try {
            Long userId = SecurityUtils.currentUser().getUserId();
            return userId == null ? OptionalLong.empty() : OptionalLong.of(userId);
        } catch (BusinessException e) {
            return OptionalLong.empty();
        }
    }
}
