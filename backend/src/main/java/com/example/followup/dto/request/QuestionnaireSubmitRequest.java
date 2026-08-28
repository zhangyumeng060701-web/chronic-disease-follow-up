/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.request;

import lombok.Data;

import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * QuestionnaireSubmitRequest 请求参数。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class QuestionnaireSubmitRequest {
    @NotNull(message = "答案不能为空")
    private Map<String, Object> answers;
}
