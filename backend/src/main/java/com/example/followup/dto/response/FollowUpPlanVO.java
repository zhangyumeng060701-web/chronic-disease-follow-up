package com.example.followup.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FollowUpPlanVO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String riskLevel;
    private Integer followUpFrequencyDays;
    private String followUpType;
    private LocalDate nextFollowUpDate;
    private String status;
    private Long doctorId;
    private String doctorName;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
