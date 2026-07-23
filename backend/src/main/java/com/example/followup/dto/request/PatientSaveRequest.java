package com.example.followup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PatientSaveRequest {
    @NotBlank(message = "name is required", groups = PatientSaveGroup.Add.class)
    private String name;

    @NotBlank(message = "gender is required", groups = PatientSaveGroup.Add.class)
    private String gender;

    private Integer age;
    private String phone;
    private String idCard;
    private String address;

    @NotBlank(message = "diseaseType is required", groups = PatientSaveGroup.Add.class)
    private String diseaseType;

    private String medicalHistory;
    private String medicationInfo;
    private Long doctorId;
}
