package com.example.followup.controller;

import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.request.PatientSaveGroup;
import com.example.followup.dto.request.PatientSaveRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.dto.response.Result;
import com.example.followup.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/patients")
@Api(tags = "鎮ｈ€呯鐞?")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    @ApiOperation("鍒嗛〉鏌ヨ鎮ｈ€呭垪琛?")
    public Result<PageResponse<PatientVO>> list(@Validated PatientQuery query, HttpServletRequest request) {
        return Result.success(patientService.listPatients(query, currentRole(request)));
    }

    @GetMapping("/{id}")
    @ApiOperation("鑾峰彇鎮ｈ€呰鎯?")
    public Result<PatientVO> getById(@PathVariable Long id, HttpServletRequest request) {
        return Result.success(patientService.getPatientById(id, currentRole(request)));
    }

    @PostMapping
    @ApiOperation("鏂板鎮ｈ€?")
    public Result<Void> add(@Validated(PatientSaveGroup.Add.class) @RequestBody PatientSaveRequest request) {
        patientService.addPatient(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("缂栬緫鎮ｈ€?")
    public Result<Void> update(@PathVariable Long id, @Validated(PatientSaveGroup.Update.class) @RequestBody PatientSaveRequest request) {
        patientService.updatePatient(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("鍒犻櫎鎮ｈ€咃紙杞垹闄わ級")
    public Result<Void> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return Result.success();
    }

    private String currentRole(HttpServletRequest request) {
        Object role = request.getAttribute("role");
        return role == null ? "" : role.toString();
    }
}
