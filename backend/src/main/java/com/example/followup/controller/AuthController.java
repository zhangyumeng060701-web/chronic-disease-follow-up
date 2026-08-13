package com.example.followup.controller;

import com.example.followup.dto.request.LoginRequest;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.SysUser;
import com.example.followup.mapper.SysUserMapper;
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

    @Autowired
    private SysUserMapper sysUserMapper;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = sysUserMapper.findByUsername(request.getUsername());
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error(403, "账号已被禁用");
        }
        boolean passwordMatch;
        if (user.getPassword().startsWith("$2a$")) {
            passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        } else {
            passwordMatch = request.getPassword().equals(user.getPassword());
        }
        if (!passwordMatch) {
            return Result.error(401, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("role", user.getRole());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        return Result.success(data);
    }
}
