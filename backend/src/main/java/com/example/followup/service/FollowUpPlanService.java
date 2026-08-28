/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.FollowUpPlanQuery;
import com.example.followup.dto.request.FollowUpPlanSaveRequest;
import com.example.followup.dto.response.FollowUpPlanVO;
import com.example.followup.dto.response.PageResponse;

/**
 * FollowUpPlanService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface FollowUpPlanService {
    PageResponse<FollowUpPlanVO> listPlans(FollowUpPlanQuery query);
    FollowUpPlanVO createPlan(FollowUpPlanSaveRequest request);
    FollowUpPlanVO updatePlan(Long id, FollowUpPlanSaveRequest request);
    void deletePlan(Long id);
}
