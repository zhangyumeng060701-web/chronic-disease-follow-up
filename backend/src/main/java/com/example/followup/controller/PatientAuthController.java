/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.dto.request.PatientLoginRequest;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.Patient;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.util.JwtUtil;
import com.example.followup.util.SensitiveDataCipher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

/**
 * PatientAuthController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/patient")
@Api(tags = "患者端认证")
public class PatientAuthController {
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SensitiveDataCipher sensitiveDataCipher;

/**
 * 执行 login 操作。
 */
    @PostMapping("/login")
    @ApiOperation(value = "患者端登录")
    public Result<Map<String, String>> login(@Valid @RequestBody PatientLoginRequest request) {
        List<Patient> patients = patientMapper.selectList(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getStatus, 1));
        Patient patient = patients.stream()
                .filter(item -> request.getPhone().equals(sensitiveDataCipher.decrypt(item.getPhone()))
                        && request.getIdCard().equals(sensitiveDataCipher.decrypt(item.getIdCard())))
                .findFirst().orElse(null);
        if (patient == null || (patient.getStatus() != null && patient.getStatus() == 0)) {
            return Result.error(ErrorCode.UNAUTHORIZED.getHttpStatus(), "患者不存在或账号已停用");
        }

        String token = jwtUtil.generateToken(patient.getName(), "PATIENT", patient.getId(), patient.getId());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("role", "PATIENT");
        data.put("patientId", String.valueOf(patient.getId()));
        data.put("name", patient.getName());
        return Result.success(data);
    }
}
