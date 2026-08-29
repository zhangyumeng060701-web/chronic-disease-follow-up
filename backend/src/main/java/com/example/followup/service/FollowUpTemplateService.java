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

    /**
     * 查询listTemplates。
     *
     * @param query 参数说明
     * @return 返回值
     */
    PageResponse<FollowUpTemplateVO> listTemplates(FollowUpTemplateQuery query);

    /**
     * 新增createTemplate。
     *
     * @param template 参数说明
     * @return 返回值
     */
    FollowUpTemplateVO createTemplate(FollowUpTemplate template);

    /**
     * 更新updateTemplate。
     *
     * @param id 参数说明
     * @param template 参数说明
     * @return 返回值
     */
    FollowUpTemplateVO updateTemplate(Long id, FollowUpTemplate template);

    /**
     * 执行toggleTemplate操作。
     *
     * @param id 参数说明
     */
    void toggleTemplate(Long id);
}
