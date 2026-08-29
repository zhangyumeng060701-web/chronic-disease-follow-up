/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.constant.DomainConstants;
import com.example.followup.dto.request.FollowUpTaskQuery;
import com.example.followup.dto.response.FollowUpTaskVO;
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
import com.example.followup.service.FollowUpTaskService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * FollowUpTaskServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class FollowUpTaskServiceImpl implements FollowUpTaskService {
    @Autowired
    private FollowUpTaskMapper taskMapper;
    @Autowired
    private FollowUpPlanMapper planMapper;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询listTasks。
     *
     * @param query 参数说明
     * @return 返回值
     */
    @Override
    public PageResponse<FollowUpTaskVO> listTasks(FollowUpTaskQuery query) {
        long start = System.currentTimeMillis();
        boolean isAdmin = SecurityUtils.isAdmin();
        Long currentUserId = isAdmin ? null : SecurityUtils.currentUser().getUserId();

        LambdaQueryWrapper<FollowUpTask> wrapper = new LambdaQueryWrapper<>();
        if (!isAdmin) {
            wrapper.eq(FollowUpTask::getOwnerId, currentUserId);
        }
        if (query.getPatientId() != null) {
            wrapper.eq(FollowUpTask::getPatientId, query.getPatientId());
        }
        if (query.getPlanId() != null) {
            wrapper.eq(FollowUpTask::getPlanId, query.getPlanId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(FollowUpTask::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getTaskType())) {
            wrapper.eq(FollowUpTask::getTaskType, query.getTaskType());
        }
        wrapper.orderByAsc(FollowUpTask::getDueDate).orderByDesc(FollowUpTask::getCreateTime);

        Page<FollowUpTask> page = new Page<>(query.getPage(), query.getSize());
        taskMapper.selectPage(page, wrapper);
        List<FollowUpTaskVO> vos = toVOs(page.getRecords());

        log.info("listTasks total={} cost={}ms", page.getTotal(), System.currentTimeMillis() - start);
        return PageResponseUtil.of(page, vos, query.getPage(), query.getSize());
    }

    /**
     * 执行completeTask操作。
     *
     * @param id 参数说明
     */
    @Override
    @Transactional
    public void completeTask(Long id) {
        FollowUpTask task = getTaskOrThrow(id);
        task.setStatus(DomainConstants.TASK_STATUS_COMPLETED);
        task.setCompletedTime(LocalDateTime.now());
        taskMapper.updateById(task);

        if (task.getPlanId() != null) {
            FollowUpPlan plan = planMapper.selectById(task.getPlanId());
            if (plan != null && DomainConstants.PLAN_STATUS_ACTIVE.equals(plan.getStatus())) {
                plan.setNextFollowUpDate(LocalDate.now().plusDays(plan.getFollowUpFrequencyDays()));
                planMapper.updateById(plan);
            }
        }
        log.info("completeTask id={}", id);
    }

    /**
     * 判断cancelTask。
     *
     * @param id 参数说明
     */
    @Override
    @Transactional
    public void cancelTask(Long id) {
        FollowUpTask task = getTaskOrThrow(id);
        task.setStatus(DomainConstants.TASK_STATUS_CANCELED);
        taskMapper.updateById(task);
        log.info("cancelTask id={}", id);
    }

    /**
     * 新增createTaskFromPlan。
     *
     * @param plan 参数说明
     */
    @Override
    @Transactional
    public void createTaskFromPlan(FollowUpPlan plan) {
        if (plan == null || plan.getPatientId() == null || plan.getNextFollowUpDate() == null) {
            return;
        }
        LambdaQueryWrapper<FollowUpTask> exists = new LambdaQueryWrapper<>();
        exists.eq(FollowUpTask::getPlanId, plan.getId())
            .eq(FollowUpTask::getDueDate, plan.getNextFollowUpDate())
            .in(FollowUpTask::getStatus,
                    DomainConstants.TASK_STATUS_PENDING,
                    DomainConstants.TASK_STATUS_IN_PROGRESS,
                    DomainConstants.TASK_STATUS_CONTACTED);
        if (taskMapper.selectCount(exists) > 0) {
            return;
        }

        FollowUpTask task = new FollowUpTask();
        task.setPlanId(plan.getId());
        task.setPatientId(plan.getPatientId());
        task.setTaskType("FOLLOW_UP");
        task.setStatus(DomainConstants.TASK_STATUS_PENDING);
        task.setOwnerId(plan.getDoctorId());
        task.setChannel(plan.getFollowUpType());
        task.setDueDate(plan.getNextFollowUpDate());
        taskMapper.insert(task);
        log.info("createTaskFromPlan planId={} dueDate={}", plan.getId(), plan.getNextFollowUpDate());
    }

    private FollowUpTask getTaskOrThrow(Long id) {
        FollowUpTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "随访任务不存在");
        }
        if (!SecurityUtils.isAdmin() && !Objects.equals(task.getOwnerId(),
                SecurityUtils.currentUser().getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return task;
    }

    private List<FollowUpTaskVO> toVOs(List<FollowUpTask> tasks) {
        if (tasks.isEmpty()) {
            return List.of();
        }
        List<Long> patientIds = tasks.stream().map(FollowUpTask::getPatientId).distinct().collect(Collectors.toList());
        List<Long> ownerIds = tasks.stream().map(FollowUpTask::getOwnerId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> patientNames = patientMapper.selectBatchIds(patientIds).stream()
                .collect(Collectors.toMap(Patient::getId, Patient::getName, (a, b) -> a));
        Map<Long, String> ownerNames = ownerIds.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(ownerIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
        return tasks.stream().map(task -> {
            FollowUpTaskVO vo = new FollowUpTaskVO();
            vo.setId(task.getId());
            vo.setPlanId(task.getPlanId());
            vo.setPatientId(task.getPatientId());
            vo.setPatientName(patientNames.getOrDefault(task.getPatientId(), ""));
            vo.setTaskType(task.getTaskType());
            vo.setStatus(task.getStatus());
            vo.setOwnerId(task.getOwnerId());
            vo.setOwnerName(ownerNames.getOrDefault(task.getOwnerId(), ""));
            vo.setChannel(task.getChannel());
            vo.setDueDate(task.getDueDate());
            vo.setCompletedTime(task.getCompletedTime());
            vo.setRemark(task.getRemark());
            vo.setCreateTime(task.getCreateTime());
            vo.setUpdateTime(task.getUpdateTime());
            return vo;
        }).collect(Collectors.toList());
    }
}
