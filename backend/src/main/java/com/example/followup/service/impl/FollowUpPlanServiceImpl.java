/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.constant.DomainConstants;
import com.example.followup.dto.request.FollowUpPlanQuery;
import com.example.followup.dto.request.FollowUpPlanSaveRequest;
import com.example.followup.dto.response.FollowUpPlanVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.entity.FollowUpPlan;
import com.example.followup.entity.FollowUpTask;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.FollowUpPlanMapper;
import com.example.followup.mapper.FollowUpTaskMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.FollowUpPlanService;
import com.example.followup.service.FollowUpTaskService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * FollowUpPlanServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class FollowUpPlanServiceImpl implements FollowUpPlanService {
    @Autowired
    private FollowUpPlanMapper planMapper;
    @Autowired
    private FollowUpTaskMapper taskMapper;
    @Autowired
    private FollowUpTaskService followUpTaskService;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

/**
 * 执行 listPlans 操作。
 */
    @Override
    public PageResponse<FollowUpPlanVO> listPlans(FollowUpPlanQuery query) {
        long start = System.currentTimeMillis();
        boolean admin = SecurityUtils.isAdmin();
        Long currentUserId = admin ? null : SecurityUtils.currentUser().getUserId();

        LambdaQueryWrapper<FollowUpPlan> wrapper = new LambdaQueryWrapper<>();
        if (!admin) {
            wrapper.eq(FollowUpPlan::getDoctorId, currentUserId);
        }
        if (query.getPatientId() != null) {
            wrapper.eq(FollowUpPlan::getPatientId, query.getPatientId());
        }
        if (StringUtils.hasText(query.getRiskLevel())) {
            wrapper.eq(FollowUpPlan::getRiskLevel, query.getRiskLevel());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(FollowUpPlan::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(FollowUpPlan::getNextFollowUpDate);

        Page<FollowUpPlan> page = new Page<>(query.getPage(), query.getSize());
        planMapper.selectPage(page, wrapper);
        List<FollowUpPlanVO> vos = toVOs(page.getRecords());

        log.info("listPlans total={} cost={}ms", page.getTotal(), System.currentTimeMillis() - start);
        return PageResponseUtil.of(page, vos, query.getPage(), query.getSize());
    }

/**
 * 执行 createPlan 操作。
 */
    @Override
    @Transactional
    public FollowUpPlanVO createPlan(FollowUpPlanSaveRequest request) {
        Patient patient = patientMapper.selectById(request.getPatientId());
        if (patient == null) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }

        FollowUpPlan plan = new FollowUpPlan();
        applyRequest(plan, request);
        plan.setStatus(StringUtils.hasText(request.getStatus())
                ? request.getStatus() : DomainConstants.PLAN_STATUS_ACTIVE);
        plan.setDoctorId(resolveDoctorId(request));
        planMapper.insert(plan);

        followUpTaskService.createTaskFromPlan(plan);
        log.info("createPlan id={} patientId={}", plan.getId(), plan.getPatientId());
        return toVO(plan);
    }

/**
 * 执行 updatePlan 操作。
 */
    @Override
    @Transactional
    public FollowUpPlanVO updatePlan(Long id, FollowUpPlanSaveRequest request) {
        FollowUpPlan plan = getPlanOrThrow(id);
        applyRequest(plan, request);
        if (StringUtils.hasText(request.getStatus())) {
            plan.setStatus(request.getStatus());
        }
        planMapper.updateById(plan);
        log.info("updatePlan id={}", id);
        return toVO(plan);
    }

/**
 * 执行 deletePlan 操作。
 */
    @Override
    @Transactional
    public void deletePlan(Long id) {
        FollowUpPlan plan = getPlanOrThrow(id);
        LambdaQueryWrapper<FollowUpTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(FollowUpTask::getPlanId, id).in(FollowUpTask::getStatus,
                DomainConstants.TASK_STATUS_PENDING,
                DomainConstants.TASK_STATUS_IN_PROGRESS,
                DomainConstants.TASK_STATUS_CONTACTED);
        List<FollowUpTask> tasks = taskMapper.selectList(taskWrapper);
        tasks.forEach(task -> task.setStatus(DomainConstants.TASK_STATUS_CANCELED));
        tasks.forEach(taskMapper::updateById);
        planMapper.deleteById(plan.getId());
        log.info("deletePlan id={}", id);
    }

    private FollowUpPlan getPlanOrThrow(Long id) {
        FollowUpPlan plan = planMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "随访计划不存在");
        }
        if (!SecurityUtils.isAdmin() && !Objects.equals(plan.getDoctorId(),
                SecurityUtils.currentUser().getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return plan;
    }

    private Long resolveDoctorId(FollowUpPlanSaveRequest request) {
        if (SecurityUtils.isAdmin() && request.getDoctorId() != null) {
            return request.getDoctorId();
        }
        return SecurityUtils.currentUser().getUserId();
    }

    private void applyRequest(FollowUpPlan plan, FollowUpPlanSaveRequest request) {
        plan.setPatientId(request.getPatientId());
        plan.setRiskLevel(request.getRiskLevel());
        plan.setFollowUpFrequencyDays(request.getFollowUpFrequencyDays());
        plan.setFollowUpType(request.getFollowUpType());
        plan.setNextFollowUpDate(request.getNextFollowUpDate());
        plan.setRemark(request.getRemark());
    }

    private List<FollowUpPlanVO> toVOs(List<FollowUpPlan> plans) {
        if (plans.isEmpty()) {
            return List.of();
        }
        List<Long> patientIds = plans.stream().map(FollowUpPlan::getPatientId).distinct().collect(Collectors.toList());
        List<Long> doctorIds = plans.stream().map(FollowUpPlan::getDoctorId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> patientNames = patientMapper.selectBatchIds(patientIds).stream()
                .collect(Collectors.toMap(Patient::getId, Patient::getName, (a, b) -> a));
        Map<Long, String> doctorNames = doctorIds.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(doctorIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
        return plans.stream().map(plan -> {
            FollowUpPlanVO vo = toVO(plan);
            vo.setPatientName(patientNames.getOrDefault(plan.getPatientId(), ""));
            vo.setDoctorName(doctorNames.getOrDefault(plan.getDoctorId(), ""));
            return vo;
        }).collect(Collectors.toList());
    }

    private FollowUpPlanVO toVO(FollowUpPlan plan) {
        FollowUpPlanVO vo = new FollowUpPlanVO();
        vo.setId(plan.getId());
        vo.setPatientId(plan.getPatientId());
        vo.setRiskLevel(plan.getRiskLevel());
        vo.setFollowUpFrequencyDays(plan.getFollowUpFrequencyDays());
        vo.setFollowUpType(plan.getFollowUpType());
        vo.setNextFollowUpDate(plan.getNextFollowUpDate());
        vo.setStatus(plan.getStatus());
        vo.setDoctorId(plan.getDoctorId());
        vo.setRemark(plan.getRemark());
        vo.setCreateTime(plan.getCreateTime());
        vo.setUpdateTime(plan.getUpdateTime());
        return vo;
    }
}
