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
 * QuestionnaireSubmission 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
@TableName("t_questionnaire_submission")
public class QuestionnaireSubmission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionnaireId;
    private Long patientId;
    private String answerJson;
    private LocalDateTime submitTime;
    private LocalDateTime createTime;
}
