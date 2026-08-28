/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.example.followup.annotation.OperationLog;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.request.PatientSaveRequest;
import com.example.followup.dto.request.PatientUpdateRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.dto.response.Result;
import com.example.followup.service.PatientService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * PatientController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/patients")
@Api(tags = "患者管理")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @GetMapping
    @ApiOperation(value = "分页查询患者列表",
            notes = "示例：GET /api/patients?page=1&size=20。错误码：400 参数错误，401 未登录，403 无权限，404 不存在，500 服务异常。")
    /**
    * 执行 list 操作。
    */
    public Result<PageResponse<PatientVO>> list(@Valid PatientQuery query) {
        return Result.success(patientService.listPatients(query));
    }

    @GetMapping(value = "/export", produces = "text/csv;charset=UTF-8")
    @ApiOperation(value = "导出患者（自动脱敏并记录审计日志）")
    public String export() {
        return patientService.exportPatientsCsv();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取患者详情",
            notes = "示例：GET /api/patients/1。错误码：401 未登录，403 无权限，404 不存在。")
    /**
    * 执行 getById 操作。
    */
    public Result<PatientVO> getById(@PathVariable Long id) {
        return Result.success(patientService.getPatientById(id));
    }

    @PostMapping
    @ApiOperation(value = "新增患者",
            notes = "请求体包含 name/gender/age/phone/idCard/address/diseaseType 等字段。错误码：400 参数错误，401 未登录，403 无权限。")
    @OperationLog(operation = "新增患者", targetType = "Patient")
    public Result<Void> add(@Valid @RequestBody PatientSaveRequest request) {
        patientService.addPatient(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "编辑患者",
            notes = "请求体与新增一致，路径 id 必填。错误码：400 参数错误，403 无权限，404 不存在。")
/**
 * 执行 update 操作。
 */
    @OperationLog(operation = "编辑患者", targetType = "Patient")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PatientUpdateRequest request) {
        patientService.updatePatient(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除患者（软删除）",
            notes = "示例：DELETE /api/patients/1。错误码：401 未登录，403 无权限，404 不存在。")
    @OperationLog(operation = "删除患者", targetType = "Patient")
    public Result<Void> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return Result.success();
    }
}
