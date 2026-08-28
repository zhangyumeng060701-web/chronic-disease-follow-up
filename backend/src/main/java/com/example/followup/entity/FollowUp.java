/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FollowUp 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
@TableName("t_follow_up")
public class FollowUp {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private LocalDate followUpDate;
    private String followUpType;
    private Integer systolicBp;
    private Integer diastolicBp;
    private BigDecimal fastingGlucose;
    private BigDecimal postprandialGlucose;
    private String medicationAdherence;
    private String symptoms;
    private String advice;
    private LocalDate nextFollowUpDate;
    private Long doctorId;
    private String sourceType;
    private LocalDateTime createTime;
}
