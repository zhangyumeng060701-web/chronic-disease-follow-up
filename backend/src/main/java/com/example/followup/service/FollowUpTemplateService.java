/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.FollowUpTemplateQuery;
import com.example.followup.dto.response.FollowUpTemplateVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.FollowUpTemplate;

/**
 * FollowUpTemplateService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface FollowUpTemplateService {
    PageResponse<FollowUpTemplateVO> listTemplates(FollowUpTemplateQuery query);
    FollowUpTemplateVO createTemplate(FollowUpTemplate template);
    FollowUpTemplateVO updateTemplate(Long id, FollowUpTemplate template);
    void toggleTemplate(Long id);
}
