package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

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
