package com.example.followup.controller;

import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.Patient;
import com.example.followup.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/patients")
@Api(tags = "患者管理")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    @ApiOperation("分页查询患者列表")
    public Result<PageResponse<Patient>> list(@Valid PatientQuery query) {
        return Result.success(patientService.listPatients(query));
    }

    @GetMapping("/{id}")
    @ApiOperation("获取患者详情")
    public Result<Patient> getById(@PathVariable Long id) {
        return Result.success(patientService.getPatientById(id));
    }

    @PostMapping
    @ApiOperation("新增患者")
    public Result<Void> add(@Valid @RequestBody Patient patient) {
        patientService.addPatient(patient);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑患者")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        patient.setId(id);
        patientService.updatePatient(patient);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除患者（软删除）")
    public Result<Void> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return Result.success();
    }
}