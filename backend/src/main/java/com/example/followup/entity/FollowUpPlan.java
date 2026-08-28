/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FollowUpPlan 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
@TableName("t_follow_up_plan")
public class FollowUpPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private String riskLevel;
    private Integer followUpFrequencyDays;
    private String followUpType;
    private LocalDate nextFollowUpDate;
    private String status;
    private Long doctorId;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
