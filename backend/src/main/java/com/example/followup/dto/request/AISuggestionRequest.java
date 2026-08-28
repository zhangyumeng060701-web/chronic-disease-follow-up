/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.request;

import lombok.Data;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * AISuggestionRequest 请求参数。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class AISuggestionRequest {
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    private List<FollowUpInput> recentFollowUps;
    private String riskLevel;
}
