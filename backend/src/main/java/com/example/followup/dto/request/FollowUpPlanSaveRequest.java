/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.request;

import lombok.Data;

import java.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * FollowUpPlanSaveRequest 请求参数。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class FollowUpPlanSaveRequest {
    @NotNull(message = "患者不能为空")
    private Long patientId;

    @NotBlank(message = "风险等级不能为空")
    private String riskLevel;

    @NotNull(message = "随访频率不能为空")
    @Min(value = 1, message = "随访频率不能小于1天")
    @Max(value = 365, message = "随访频率不能超过365天")
    private Integer followUpFrequencyDays;

    @NotBlank(message = "随访方式不能为空")
    private String followUpType;

    @NotNull(message = "下次随访日期不能为空")
    private LocalDate nextFollowUpDate;

    private String status;
    private String remark;
    private Long doctorId;
}
