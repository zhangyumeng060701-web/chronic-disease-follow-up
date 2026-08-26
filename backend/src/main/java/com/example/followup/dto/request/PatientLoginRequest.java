package com.example.followup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PatientLoginRequest {
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "身份证号不能为空")
    private String idCard;
}
