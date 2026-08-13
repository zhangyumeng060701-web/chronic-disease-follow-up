package com.example.followup.service;

import com.example.followup.dto.response.PatientVO;
import com.example.followup.entity.Patient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class PatientMaskingService {
    public PatientVO toVO(Patient patient, boolean admin) {
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(patient, vo);
        if (!admin) {
            vo.setName(maskName(vo.getName()));
            vo.setPhone(maskPhone(vo.getPhone()));
            vo.setIdCard(maskIdCard(vo.getIdCard()));
            vo.setAddress(maskAddress(vo.getAddress()));
        }
        return vo;
    }

    public String maskName(String value) {
        if (value == null || value.isEmpty()) return value;
        return value.substring(0, 1) + "*".repeat(Math.max(0, value.length() - 1));
    }

    public String maskPhone(String value) {
        if (value == null || value.isEmpty()) return value;
        if (value.length() < 7) return "*".repeat(value.length());
        return value.substring(0, 3) + "*".repeat(value.length() - 7) + value.substring(value.length() - 4);
    }

    public String maskIdCard(String value) {
        if (value == null || value.isEmpty()) return value;
        if (value.length() < 10) return "*".repeat(value.length());
        return value.substring(0, 6) + "*".repeat(value.length() - 10) + value.substring(value.length() - 4);
    }

    public String maskAddress(String value) {
        if (value == null || value.isEmpty()) return value;
        int district = Math.max(value.indexOf('区'), value.indexOf('县'));
        return district >= 0 ? value.substring(0, district + 1) + "****" : "****";
    }
}
