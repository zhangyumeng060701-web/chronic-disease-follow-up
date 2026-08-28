/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.example.followup.dto.response.DoctorStats;
import com.example.followup.dto.response.Result;
import com.example.followup.dto.response.StatsOverview;
import com.example.followup.dto.response.TrendItem;
import com.example.followup.service.StatsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * StatsController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/stats")
@Api(tags = "数据统计")
public class StatsController {
    @Autowired
    private StatsService statsService;

    @GetMapping("/overview")
    @ApiOperation(value = "获取总览数据",
            notes = "返回患者总数、随访完成率、高危数、失访数。错误码：401 未登录，403 无权限。")
/**
 * 执行 overview 操作。
 */
    public Result<StatsOverview> overview() {
        return Result.success(statsService.getOverview());
    }

    @GetMapping("/bp-trend")
    @ApiOperation(value = "血压控制率趋势",
            notes = "返回近 12 个月血压控制率。错误码：401 未登录，403 无权限。")
    public Result<List<TrendItem>> bpTrend() {
        return Result.success(statsService.getBpTrend());
    }

    @GetMapping("/glucose-trend")
    @ApiOperation(value = "血糖控制率趋势",
            notes = "返回近 12 个月血糖控制率。错误码：401 未登录，403 无权限。")
/**
 * 执行 glucoseTrend 操作。
 */
    public Result<List<TrendItem>> glucoseTrend() {
        return Result.success(statsService.getGlucoseTrend());
    }

    @GetMapping("/doctor-comparison")
    @ApiOperation(value = "医生对比数据",
            notes = "医生角色只返回自己数据，管理员返回全部医生。错误码：401 未登录，403 无权限。")
    public Result<List<DoctorStats>> doctorComparison() {
        return Result.success(statsService.getDoctorComparison());
    }
}
