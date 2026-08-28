/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FollowUpTaskVO 返回模型。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class FollowUpTaskVO {
    private Long id;
    private Long planId;
    private Long patientId;
    private String patientName;
    private String taskType;
    private String status;
    private Long ownerId;
    private String ownerName;
    private String channel;
    private LocalDate dueDate;
    private LocalDateTime completedTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
