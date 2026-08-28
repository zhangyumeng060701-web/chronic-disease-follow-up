/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.request;

import lombok.Data;

import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * PatientUpdateRequest 请求参数。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class PatientUpdateRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    @Min(value = 0, message = "年龄不能小于0")
    @Max(value = 150, message = "年龄不能超过150")
    private Integer age;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "^$|^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    private String idCard;

    @Size(max = 200, message = "住址长度不能超过200")
    private String address;

    @NotBlank(message = "慢病类型不能为空")
    private String diseaseType;

    @Size(max = 1000, message = "病史长度不能超过1000")
    private String medicalHistory;

    @Size(max = 1000, message = "用药信息长度不能超过1000")
    private String medicationInfo;

    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String smoking;
    private String drinking;
    @Size(max = 1000, message = "过敏史长度不能超过1000")
    private String allergyHistory;
    @Size(max = 1000, message = "用药史长度不能超过1000")
    private String medicationHistory;

    private Long doctorId;
}
