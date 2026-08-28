/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.constant.DomainConstants;
import com.example.followup.entity.FollowUpPlan;
import com.example.followup.mapper.FollowUpPlanMapper;
import com.example.followup.service.FollowUpTaskService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * FollowUpTaskScheduler 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Component
public class FollowUpTaskScheduler {
    @Autowired
    private FollowUpPlanMapper planMapper;
    @Autowired
    private FollowUpTaskService taskService;

    @Scheduled(cron = "0 30 1 * * ?")
    public void generateDueTasks() {
        long start = System.currentTimeMillis();
        List<FollowUpPlan> duePlans = planMapper.selectList(new LambdaQueryWrapper<FollowUpPlan>()
                .eq(FollowUpPlan::getStatus, DomainConstants.PLAN_STATUS_ACTIVE)
                .le(FollowUpPlan::getNextFollowUpDate, LocalDate.now()));
        duePlans.forEach(taskService::createTaskFromPlan);
        log.info("generateDueTasks plans={} cost={}ms", duePlans.size(), System.currentTimeMillis() - start);
    }
}
