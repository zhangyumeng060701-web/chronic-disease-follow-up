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
    PageResponse<FollowUpVO> listFollowUps(FollowUpQuery query);
    FollowUp getFollowUpById(Long id);
    void addFollowUp(FollowUp followUp);
    void updateFollowUp(FollowUp followUp);
    void deleteFollowUp(Long id);
    /** 查询逾期未随访的患者记录（next_follow_up_date 早于当前日期） */
    List<FollowUpVO> listOverdueFollowUps();
}
