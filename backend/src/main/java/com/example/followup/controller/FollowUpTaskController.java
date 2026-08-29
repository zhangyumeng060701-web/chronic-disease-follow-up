/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.example.followup.dto.request.FollowUpTaskQuery;
import com.example.followup.dto.response.FollowUpTaskVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.service.FollowUpTaskService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * FollowUpTaskController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/follow-up-tasks")
@Api(tags = "随访任务")
public class FollowUpTaskController {
    @Autowired
    private FollowUpTaskService taskService;

    @GetMapping
    @ApiOperation(value = "分页查询随访任务")
    /**
     * 分页查询随访任务
     *
     * @param query 参数说明
     * @return 返回值
     */
    public Result<PageResponse<FollowUpTaskVO>> list(@Valid FollowUpTaskQuery query) {
        return Result.success(taskService.listTasks(query));
    }

    /**
     * 执行 complete 操作。
     */
    @PutMapping("/{id}/complete")
    @ApiOperation(value = "完成任务")
    public Result<Void> complete(@PathVariable Long id) {
        taskService.completeTask(id);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @ApiOperation(value = "取消任务")
    public Result<Void> cancel(@PathVariable Long id) {
        taskService.cancelTask(id);
        return Result.success();
    }
}
