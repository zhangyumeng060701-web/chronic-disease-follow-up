/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * UpdateUserRequest 请求参数。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class UpdateUserRequest {

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "角色不能为空")
    private String role;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "^$|^.{6,64}$", message = "密码长度需在6到64位之间")
    private String password;
}
