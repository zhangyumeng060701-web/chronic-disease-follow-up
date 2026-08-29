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
    /**
     * 查询listPlans。
     *
     * @param query 参数说明
     * @return 返回值
     */
    PageResponse<FollowUpPlanVO> listPlans(FollowUpPlanQuery query);
    /**
     * 新增createPlan。
     *
     * @param request 参数说明
     * @return 返回值
     */
    FollowUpPlanVO createPlan(FollowUpPlanSaveRequest request);
    /**
     * 更新updatePlan。
     *
     * @param id 参数说明
     * @param request 参数说明
     * @return 返回值
     */
    FollowUpPlanVO updatePlan(Long id, FollowUpPlanSaveRequest request);
    /**
     * 删除deletePlan。
     *
     * @param id 参数说明
     */
    void deletePlan(Long id);
}
