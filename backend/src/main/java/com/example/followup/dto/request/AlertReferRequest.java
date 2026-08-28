/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * AlertReferRequest 请求参数。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class AlertReferRequest {
    @NotBlank(message = "转诊原因不能为空")
    @Size(max = 500, message = "转诊原因不能超过500字")
    private String referralReason;
}
