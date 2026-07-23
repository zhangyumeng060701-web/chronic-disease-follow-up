package com.example.followup.dto.response;

import com.example.followup.entity.Patient;
import com.example.followup.util.PatientDesensitizationUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

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
    private Long doctorId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static PatientVO from(Patient patient, String role) {
        if (patient == null) {
            return null;
        }
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(patient, vo);
        if (!PatientDesensitizationUtil.isAdmin(role)) {
            vo.setName(PatientDesensitizationUtil.maskName(patient.getName()));
            vo.setPhone(PatientDesensitizationUtil.maskPhone(patient.getPhone()));
            vo.setIdCard(PatientDesensitizationUtil.maskIdCard(patient.getIdCard()));
            vo.setAddress(PatientDesensitizationUtil.maskAddress(patient.getAddress()));
        }
        return vo;
    }
}
