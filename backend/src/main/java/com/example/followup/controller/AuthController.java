package com.example.followup.controller;

import com.example.followup.dto.request.LoginRequest;
import com.example.followup.dto.response.Result;
import com.example.followup.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        // 简化版：admin/123456 或 doctor/123456
        boolean valid = false;
        String role = "";
        if ("admin".equals(request.getUsername()) && "123456".equals(request.getPassword())) {
            valid = true;
            role = "ADMIN";
        } else if ("doctor".equals(request.getUsername()) && "123456".equals(request.getPassword())) {
            valid = true;
            role = "DOCTOR";
        }
        if (!valid) {
            return Result.error(401, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(request.getUsername(), role);
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("role", role);
        return Result.success(data);
    }
}