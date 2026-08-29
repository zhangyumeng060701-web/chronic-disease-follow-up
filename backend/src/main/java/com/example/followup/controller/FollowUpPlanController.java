/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.example.followup.dto.request.FollowUpPlanQuery;
import com.example.followup.dto.request.FollowUpPlanSaveRequest;
import com.example.followup.dto.response.FollowUpPlanVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.service.FollowUpPlanService;

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
 * FollowUpPlanController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/plans")
@Api(tags = "随访计划")
public class FollowUpPlanController {
    @Autowired
    private FollowUpPlanService planService;

    /**
     * 分页查询随访计划
     *
     * @param query 参数说明
     * @return 返回值
     */
    @GetMapping
    @ApiOperation(value = "分页查询随访计划")
    public Result<PageResponse<FollowUpPlanVO>> list(@Valid FollowUpPlanQuery query) {
        return Result.success(planService.listPlans(query));
    }

    /**
     * 新增随访计划
     *
     * @param request 参数说明
     * @return 返回值
     */
    @PostMapping
    @ApiOperation(value = "新增随访计划")
    public Result<FollowUpPlanVO> create(@Valid @RequestBody FollowUpPlanSaveRequest request) {
        return Result.success(planService.createPlan(request));
    }

    /**
     * 更新随访计划
     *
     * @param id 参数说明
     * @param request 参数说明
     * @return 返回值
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "更新随访计划")
    public Result<FollowUpPlanVO> update(@PathVariable Long id,
                                         @Valid @RequestBody FollowUpPlanSaveRequest request) {
        return Result.success(planService.updatePlan(id, request));
    }

    /**
     * 删除随访计划
     *
     * @param id 参数说明
     * @return 返回值
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除随访计划")
    public Result<Void> delete(@PathVariable Long id) {
        planService.deletePlan(id);
        return Result.success();
    }
}
