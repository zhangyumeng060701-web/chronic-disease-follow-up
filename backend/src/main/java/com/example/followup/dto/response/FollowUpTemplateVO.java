/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * FollowUpTemplateVO 返回模型。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class FollowUpTemplateVO {
    private Long id;
    private String templateCode;
    private String templateName;
    private String riskLevel;
    private Integer frequencyDays;
    private String followUpType;
    private String defaultContent;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
