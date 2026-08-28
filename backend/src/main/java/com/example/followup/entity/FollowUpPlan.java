package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
