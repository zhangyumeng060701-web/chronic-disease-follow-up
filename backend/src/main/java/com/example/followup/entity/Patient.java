package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@TableName("t_patient")
public class Patient {

    @TableId(type = IdType.AUTO)
    private Long id;

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

    private Long doctorId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
