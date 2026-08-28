/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.controller;

import com.example.followup.dto.request.LoginRequest;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.SysUser;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.util.JwtUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

/**
 * AuthController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
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

    @PostMapping("/login")
    @ApiOperation(value = "用户登录",
            notes = "请求体包含 username/password。错误码：400 参数错误，401 用户名或密码错误，403 账号禁用。")
/**
 * 执行 login 操作。
 */
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
}
