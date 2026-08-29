/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.FollowUpQuery;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.FollowUp;

import java.util.List;

/**
 * FollowUpService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface FollowUpService {
    /**
     * 查询listFollowUps。
     *
     * @param query 参数说明
     * @return 返回值
     */
    PageResponse<FollowUpVO> listFollowUps(FollowUpQuery query);
    /**
     * 查询getFollowUpById。
     *
     * @param id 参数说明
     * @return 返回值
     */
    FollowUp getFollowUpById(Long id);
    /**
     * 新增addFollowUp。
     *
     * @param followUp 参数说明
     */
    void addFollowUp(FollowUp followUp);
    /**
     * 更新updateFollowUp。
     *
     * @param followUp 参数说明
     */
    void updateFollowUp(FollowUp followUp);
    /**
     * 删除deleteFollowUp。
     *
     * @param id 参数说明
     */
    void deleteFollowUp(Long id);
    /** 查询逾期未随访的患者记录（next_follow_up_date 早于当前日期） */
    /**
     * 查询listOverdueFollowUps。
     *
     * @return 返回值
     */
    List<FollowUpVO> listOverdueFollowUps();
}
