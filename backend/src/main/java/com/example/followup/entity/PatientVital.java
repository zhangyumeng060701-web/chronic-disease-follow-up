package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_patient_vitals")
public class PatientVital {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private String metricType;
    private BigDecimal metricValue;
    private LocalDateTime measuredAt;
    private String sourceType;
    private String remark;
    private LocalDateTime createTime;
}
