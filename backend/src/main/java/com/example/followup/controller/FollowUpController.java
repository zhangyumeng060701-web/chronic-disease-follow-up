/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.example.followup.annotation.OperationLog;
import com.example.followup.dto.request.FollowUpQuery;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.FollowUp;
import com.example.followup.service.FollowUpService;

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
 * FollowUpController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/follow-ups")
@Api(tags = "随访记录管理")
public class FollowUpController {
    @Autowired
    private FollowUpService followUpService;

    @GetMapping
    @ApiOperation(value = "分页查询随访记录",
            notes = "示例：GET /api/follow-ups?page=1&size=20&startDate=2026-08-01&endDate=2026-08-31。错误码：400 参数错误，401 未登录，403 无权限。")
    /**
    * 执行 list 操作。
    */
    public Result<PageResponse<FollowUpVO>> list(@Valid FollowUpQuery query) {
        return Result.success(followUpService.listFollowUps(query));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取随访记录详情",
            notes = "示例：GET /api/follow-ups/1。错误码：401 未登录，403 无权限，404 不存在。")
    public Result<FollowUp> getById(@PathVariable Long id) {
        return Result.success(followUpService.getFollowUpById(id));
    }

    @PostMapping
    @ApiOperation(value = "新增随访记录",
            notes = "请求体包含 patientId/followUpDate/followUpType 等字段。错误码：400 参数错误，401 未登录，403 无权限。")
/**
 * 执行 add 操作。
 */
    @OperationLog(operation = "新增随访记录", targetType = "FollowUp")
    public Result<Void> add(@Valid @RequestBody FollowUp followUp) {
        followUpService.addFollowUp(followUp);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "编辑随访记录",
            notes = "请求体与新增一致，路径 id 必填。错误码：400 参数错误，403 无权限，404 不存在。")
    @OperationLog(operation = "编辑随访记录", targetType = "FollowUp")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FollowUp followUp) {
        followUp.setId(id);
        followUpService.updateFollowUp(followUp);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除随访记录",
            notes = "示例：DELETE /api/follow-ups/1。错误码：401 未登录，403 无权限，404 不存在。")
/**
 * 执行 delete 操作。
 */
    @OperationLog(operation = "删除随访记录", targetType = "FollowUp")
    public Result<Void> delete(@PathVariable Long id) {
        followUpService.deleteFollowUp(id);
        return Result.success();
    }

    @GetMapping("/overdue")
    @ApiOperation(value = "查询逾期未随访的患者",
            notes = "返回超期 7 天以上记录。错误码：401 未登录，403 无权限。")
    public Result<?> getOverdue() {
        return Result.success(followUpService.listOverdueFollowUps());
    }
}
