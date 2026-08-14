package com.example.followup.dto.request;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class PatientSaveRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "性别不能为空")
    private String gender;
    @Min(value = 0, message = "年龄不能小于0")
    @Max(value = 150, message = "年龄不能大于150")
    private Integer age;
    private String phone;
    private String idCard;
    private String address;
    @NotBlank(message = "慢病类型不能为空")
    private String diseaseType;
    private String medicalHistory;
    private String medicationInfo;
    private Long doctorId;
}
