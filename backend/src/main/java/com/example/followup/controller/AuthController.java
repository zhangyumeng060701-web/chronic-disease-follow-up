package com.example.followup.controller;

import com.example.followup.dto.request.LoginRequest;
import com.example.followup.dto.request.PatientLoginRequest;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "认证管理")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PatientMapper patientMapper;

    @PostMapping("/login")
    @ApiOperation(value = "用户登录",
            notes = "请求体包含 username/password。错误码：400 参数错误，401 用户名或密码错误，403 账号禁用。")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = sysUserMapper.findByUsername(request.getUsername());
        if (user == null) {
            return Result.error(ErrorCode.USER_PASSWORD_WRONG.getHttpStatus(),
                    ErrorCode.USER_PASSWORD_WRONG.getDefaultMessage());
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error(ErrorCode.USER_DISABLED.getHttpStatus(),
                    ErrorCode.USER_DISABLED.getDefaultMessage());
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.error(ErrorCode.USER_PASSWORD_WRONG.getHttpStatus(),
                    ErrorCode.USER_PASSWORD_WRONG.getDefaultMessage());
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("role", user.getRole());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        return Result.success(data);
    }

    @PostMapping("/patient/login")
    @ApiOperation(value = "患者端登录",
            notes = "使用患者手机号与身份证号登录。错误码：400 参数错误，401 患者不存在或已停用。")
    public Result<Map<String, String>> patientLogin(@Valid @RequestBody PatientLoginRequest request) {
        Patient patient = patientMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Patient>()
                        .eq(Patient::getPhone, request.getPhone())
                        .eq(Patient::getIdCard, request.getIdCard())
                        .last("LIMIT 1"));
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
