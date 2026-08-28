/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * PatientLoginRequest 请求参数。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class PatientLoginRequest {
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "身份证号不能为空")
    private String idCard;
}
