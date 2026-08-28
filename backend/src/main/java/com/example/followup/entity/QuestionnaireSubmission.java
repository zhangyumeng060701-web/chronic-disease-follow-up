package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.time.LocalDateTime;

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
