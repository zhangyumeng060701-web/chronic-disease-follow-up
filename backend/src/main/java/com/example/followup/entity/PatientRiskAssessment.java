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
 * PatientRiskAssessment 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
@TableName("t_patient_risk_assessment")
public class PatientRiskAssessment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private String riskLevel;
    private Integer score;
    private String evidence;
    private Long assessedBy;
    private LocalDateTime assessedAt;
    private LocalDateTime createTime;
}
