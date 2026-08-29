/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.constant.DomainConstants;
import com.example.followup.dto.request.AlertQuery;
import com.example.followup.dto.response.AlertVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.entity.Alert;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.AlertService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AlertServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class AlertServiceImpl implements AlertService {
    @Autowired
    private AlertMapper alertMapper;
    @Autowired
    private PatientMapper patientMapper;

    /**
     * 查询listAlerts。
     *
     * @param query 参数说明
     * @return 返回值
     */
    @Override
    public PageResponse<AlertVO> listAlerts(AlertQuery query) {
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        if (query.getAlertType() != null && !query.getAlertType().isEmpty()) {
            wrapper.eq(Alert::getAlertType, query.getAlertType());
        }
        if (query.getAlertLevel() != null && !query.getAlertLevel().isEmpty()) {
            wrapper.eq(Alert::getAlertLevel, query.getAlertLevel());
        }
        if (query.getIsResolved() != null) {
            wrapper.eq(Alert::getIsResolved, query.getIsResolved());
        }
        if (query.getAlertStatus() != null && !query.getAlertStatus().isEmpty()) {
            wrapper.eq(Alert::getAlertStatus, query.getAlertStatus());
        }
        wrapper.orderByDesc(Alert::getCreateTime);

        Page<Alert> page = new Page<>(query.getPage(), query.getSize());
        alertMapper.selectPage(page, wrapper);

        List<Long> patientIds = page.getRecords().stream()
                .map(Alert::getPatientId).distinct().collect(Collectors.toList());
        Map<Long, String> nameMap = patientIds.isEmpty()
                ? Map.of()
                : patientMapper.selectBatchIds(patientIds).stream()
                        .collect(Collectors.toMap(Patient::getId, Patient::getName));

        List<AlertVO> vos = page.getRecords().stream().map(a -> {
            AlertVO vo = new AlertVO();
            vo.setId(a.getId());
            vo.setPatientId(a.getPatientId());
            vo.setPatientName(nameMap.getOrDefault(a.getPatientId(), ""));
            vo.setAlertType(a.getAlertType());
            vo.setAlertLevel(a.getAlertLevel());
            vo.setAlertReason(a.getAlertReason());
            vo.setIsResolved(a.getIsResolved());
            vo.setAlertStatus(a.getAlertStatus());
            vo.setContactTime(a.getContactTime());
            vo.setReferralReason(a.getReferralReason());
            vo.setRecommendedActions(a.getRecommendedActions());
            vo.setRecheckItems(a.getRecheckItems());
            vo.setReferralConditions(a.getReferralConditions());
            vo.setEvidenceSource(a.getEvidenceSource());
            vo.setRiskLevel(a.getRiskLevel());
            vo.setResolveTime(a.getResolveTime());
            vo.setCreateTime(a.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        long start = System.currentTimeMillis();
        log.info("listAlerts total={} cost={}ms", page.getTotal(), System.currentTimeMillis() - start);
        return PageResponseUtil.of(page, vos, query.getPage(), query.getSize());
    }

    /**
     * 执行contactAlert操作。
     *
     * @param id 参数说明
     */
    @Override
    public void contactAlert(Long id) {
        Alert alert = getAlertOrThrow(id);
        alert.setAlertStatus(DomainConstants.ALERT_STATUS_CONTACTED);
        alert.setContactTime(LocalDateTime.now());
        alertMapper.updateById(alert);
        log.info("contactAlert id={}", id);
    }

    /**
     * 解析resolveAlert。
     *
     * @param id 参数说明
     */
    @Override
    public void resolveAlert(Long id) {
        Alert alert = getAlertOrThrow(id);
        alert.setIsResolved(1);
        alert.setAlertStatus(DomainConstants.ALERT_STATUS_RESOLVED);
        alert.setResolveTime(LocalDateTime.now());
        alertMapper.updateById(alert);
        log.info("resolveAlert id={}", id);
    }

    /**
     * 执行referAlert操作。
     *
     * @param id 参数说明
     * @param referralReason 参数说明
     */
    @Override
    public void referAlert(Long id, String referralReason) {
        Alert alert = getAlertOrThrow(id);
        alert.setIsResolved(1);
        alert.setAlertStatus(DomainConstants.ALERT_STATUS_REFERRED);
        alert.setReferralReason(referralReason);
        alert.setResolveTime(LocalDateTime.now());
        alertMapper.updateById(alert);
        log.info("referAlert id={}", id);
    }

    private Alert getAlertOrThrow(Long id) {
        Alert alert = alertMapper.selectById(id);
        if (alert == null) {
            throw new BusinessException(ErrorCode.ALERT_NOT_FOUND);
        }
        return alert;
    }
}
