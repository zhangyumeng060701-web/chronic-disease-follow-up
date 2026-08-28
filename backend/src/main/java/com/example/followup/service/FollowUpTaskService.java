/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.FollowUpTaskQuery;
import com.example.followup.dto.response.FollowUpTaskVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.FollowUpPlan;

/**
 * FollowUpTaskService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface FollowUpTaskService {
    PageResponse<FollowUpTaskVO> listTasks(FollowUpTaskQuery query);
    void completeTask(Long id);
    void cancelTask(Long id);
    void createTaskFromPlan(FollowUpPlan plan);
}
