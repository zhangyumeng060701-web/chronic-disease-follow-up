/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.PatientVital;
import com.example.followup.mapper.PatientVitalMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * VitalTrendController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/vitals")
@Api(tags = "指标趋势")
public class VitalTrendController {
    @Autowired
    private PatientVitalMapper vitalMapper;

    @GetMapping("/trend")
    @ApiOperation(value = "查询患者指标长期趋势")
    public Result<List<PatientVital>> trend(@RequestParam Long patientId,
                                            @RequestParam(required = false) String metricType,
                                            @RequestParam(defaultValue = "90") Integer days) {
        LambdaQueryWrapper<PatientVital> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientVital::getPatientId, patientId)
                .ge(PatientVital::getMeasuredAt, LocalDateTime.now().minusDays(days));
        if (StringUtils.hasText(metricType)) {
            wrapper.eq(PatientVital::getMetricType, metricType);
        }
        wrapper.orderByAsc(PatientVital::getMeasuredAt);
        return Result.success(vitalMapper.selectList(wrapper));
    }
}
