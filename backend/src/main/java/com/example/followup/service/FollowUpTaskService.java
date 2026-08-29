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

    /**
     * 查询listTasks。
     *
     * @param query 参数说明
     * @return 返回值
     */
    PageResponse<FollowUpTaskVO> listTasks(FollowUpTaskQuery query);

    /**
     * 执行completeTask操作。
     *
     * @param id 参数说明
     */
    void completeTask(Long id);

    /**
     * 判断cancelTask。
     *
     * @param id 参数说明
     */
    void cancelTask(Long id);

    /**
     * 新增createTaskFromPlan。
     *
     * @param plan 参数说明
     */
    void createTaskFromPlan(FollowUpPlan plan);
}
