/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserVO 返回模型。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String role;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
}
