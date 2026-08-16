package com.example.followup.controller;

import com.example.followup.annotation.OperationLog;
import com.example.followup.dto.request.CreateUserRequest;
import com.example.followup.dto.request.UpdateUserRequest;
import com.example.followup.dto.request.UserQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.dto.response.UserVO;
import com.example.followup.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping
    @ApiOperation("分页查询用户列表")
    public Result<PageResponse<UserVO>> list(@Valid UserQuery query) {
        return Result.success(sysUserService.listUsers(query));
    }

    @PostMapping
    @ApiOperation("新增用户")
    @OperationLog(operation = "新增用户", targetType = "User")
    public Result<Void> create(@Valid @RequestBody CreateUserRequest request) {
        sysUserService.createUser(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑用户")
    @OperationLog(operation = "编辑用户", targetType = "User")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        sysUserService.updateUser(id, request);
        return Result.success();
    }

    @PutMapping("/{id}/toggle-status")
    @ApiOperation("启用/禁用用户")
    @OperationLog(operation = "切换用户状态", targetType = "User")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        sysUserService.toggleUserStatus(id);
        return Result.success();
    }
}
