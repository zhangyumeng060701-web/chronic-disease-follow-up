/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * VitalReportRequest 请求参数。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class VitalReportRequest {
    @NotBlank(message = "指标类型不能为空")
    private String metricType;

    @NotNull(message = "指标值不能为空")
    @DecimalMin(value = "0.1", message = "指标值必须大于0")
    private BigDecimal metricValue;

    @NotNull(message = "测量时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime measuredAt;

    private String remark;
}
