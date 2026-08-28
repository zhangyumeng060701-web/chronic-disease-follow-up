/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * FollowUpTaskQuery 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class FollowUpTaskQuery {
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Integer size = 20;

    private Long patientId;
    private Long planId;
    private String status;
    private String taskType;
}
