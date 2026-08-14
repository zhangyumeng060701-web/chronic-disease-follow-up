package com.example.followup.controller;

import com.example.followup.dto.request.CreateUserRequest;
import com.example.followup.dto.request.UserQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.SysUser;
import com.example.followup.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping
    @ApiOperation("分页查询用户列表")
    public Result<PageResponse<SysUser>> list(@Valid UserQuery query) {
        return Result.success(sysUserService.listUsers(query));
    }

    @PostMapping
    @ApiOperation("新增用户")
    public Result<Void> create(@Valid @RequestBody CreateUserRequest request) {
        sysUserService.createUser(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑用户")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        sysUserService.updateUser(user);
        return Result.success();
    }

    @PutMapping("/{id}/toggle-status")
    @ApiOperation("启用/禁用用户")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        sysUserService.toggleUserStatus(id);
        return Result.success();
    }
}
