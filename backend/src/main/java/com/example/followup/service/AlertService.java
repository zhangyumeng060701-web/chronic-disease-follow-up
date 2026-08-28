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
    PageResponse<AlertVO> listAlerts(AlertQuery query);
    void contactAlert(Long id);
    void resolveAlert(Long id);
    void referAlert(Long id, String referralReason);
}
