/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.AlertQuery;
import com.example.followup.dto.response.AlertVO;
import com.example.followup.dto.response.PageResponse;

/**
 * AlertService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface AlertService {
    /**
     * 查询listAlerts。
     *
     * @param query 参数说明
     * @return 返回值
     */
    PageResponse<AlertVO> listAlerts(AlertQuery query);
    /**
     * 执行contactAlert操作。
     *
     * @param id 参数说明
     */
    void contactAlert(Long id);
    /**
     * 解析resolveAlert。
     *
     * @param id 参数说明
     */
    void resolveAlert(Long id);
    /**
     * 执行referAlert操作。
     *
     * @param id 参数说明
     * @param referralReason 参数说明
     */
    void referAlert(Long id, String referralReason);
}
