/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * MessageCreateRequest 请求参数。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class MessageCreateRequest {
    @NotBlank(message = "接收方类型不能为空")
    private String recipientType;

    @NotNull(message = "接收方ID不能为空")
    private Long recipientId;

    @NotBlank(message = "渠道不能为空")
    private String channel;

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题不能超过100字")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Size(max = 1000, message = "内容不能超过1000字")
    private String content;

    private String templateCode;
}
