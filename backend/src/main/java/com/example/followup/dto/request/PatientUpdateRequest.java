package com.example.followup.dto.request;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class PatientUpdateRequest {
    private String name;
    private String gender;
    @Min(value = 0, message = "年龄不能小于0")
    @Max(value = 150, message = "年龄不能大于150")
    private Integer age;
    private String phone;
    private String idCard;
    private String address;
    private String diseaseType;
    private String medicalHistory;
    private String medicationInfo;
    private Long doctorId;
}
