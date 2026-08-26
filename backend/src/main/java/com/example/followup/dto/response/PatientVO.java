package com.example.followup.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class PatientVO {

    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String phone;
    private String idCard;
    private String address;
    private String diseaseType;
    private String medicalHistory;
    private String medicationInfo;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal bmi;
    private String smoking;
    private String drinking;
    private String allergyHistory;
    private String medicationHistory;
    private Long doctorId;
    private String doctorName;
    private LocalDate lastFollowUpDate;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
