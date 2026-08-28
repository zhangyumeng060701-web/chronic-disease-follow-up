/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.example.followup.dto.request.AlertQuery;
import com.example.followup.dto.request.AlertReferRequest;
import com.example.followup.dto.response.AlertVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.service.AlertService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * AlertController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/alerts")
@Api(tags = "预警管理")
public class AlertController {
    @Autowired
    private AlertService alertService;

    @GetMapping
    @ApiOperation(value = "分页查询预警列表",
            notes = "示例：GET /api/alerts?page=1&size=20&alertLevel=RED。错误码：400 参数错误，401 未登录，403 无权限。")
    /**
    * 执行 list 操作。
    */
    public Result<PageResponse<AlertVO>> list(@Valid AlertQuery query) {
        return Result.success(alertService.listAlerts(query));
    }

    @PutMapping("/{id}/resolve")
    @ApiOperation(value = "处理预警",
            notes = "示例：PUT /api/alerts/1/resolve。错误码：401 未登录，403 无权限，404 不存在。")
    public Result<Void> resolve(@PathVariable Long id) {
        alertService.resolveAlert(id);
        return Result.success();
    }

    @PutMapping("/{id}/contact")
    @ApiOperation(value = "标记预警为已联系",
            notes = "示例：PUT /api/alerts/1/contact。错误码：401 未登录，403 无权限，404 不存在。")
    /**
    * 执行 contact 操作。
    */
    public Result<Void> contact(@PathVariable Long id) {
        alertService.contactAlert(id);
        return Result.success();
    }

    @PutMapping("/{id}/refer")
    @ApiOperation(value = "预警转门诊",
            notes = "示例：PUT /api/alerts/1/refer，body 传入 referralReason。")
    public Result<Void> refer(@PathVariable Long id, @Valid @RequestBody AlertReferRequest request) {
        alertService.referAlert(id, request.getReferralReason());
        return Result.success();
    }
}
