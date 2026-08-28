/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.example.followup.dto.request.LogQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.OperationLog;
import com.example.followup.service.OperationLogService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * LogController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/logs")
@Api(tags = "操作日志")
public class LogController {
    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    @ApiOperation(value = "分页查询操作日志",
            notes = "示例：GET /api/logs?page=1&size=20。错误码：400 参数错误，401 未登录，403 无权限。")
    /**
    * 执行 list 操作。
    */
    public Result<PageResponse<OperationLog>> list(@Valid LogQuery query) {
        return Result.success(operationLogService.listLogs(query));
    }
}
