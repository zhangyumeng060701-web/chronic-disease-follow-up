/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Alert 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
@TableName("t_alert")
public class Alert {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private String alertType;
    private String alertLevel;
    private String alertReason;
    private Integer isResolved;
    private String alertStatus;
    private LocalDateTime contactTime;
    private String referralReason;
    private String recommendedActions;
    private String recheckItems;
    private String referralConditions;
    private String evidenceSource;
    private String riskLevel;
    private LocalDateTime resolveTime;
    private LocalDateTime createTime;
}
