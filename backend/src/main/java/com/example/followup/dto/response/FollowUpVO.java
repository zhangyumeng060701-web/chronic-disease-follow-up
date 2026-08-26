package com.example.followup.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FollowUpVO {
    private Long id;
    private Long patientId;
    private String patientName;
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
