package com.example.followup.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

@Data
public class PatientFollowUpRequest {
    @NotNull(message = "随访日期不能为空")
    private LocalDate followUpDate;

    private Integer systolicBp;
    private Integer diastolicBp;
    private BigDecimal fastingGlucose;
    private BigDecimal postprandialGlucose;
    private String medicationAdherence;
    private String symptoms;
    private String advice;
    private LocalDate nextFollowUpDate;
}
