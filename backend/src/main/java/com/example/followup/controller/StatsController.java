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

@RestController
@RequestMapping("/api/stats")
@Api(tags = "数据统计")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/overview")
    @ApiOperation("获取总览数据")
    public Result<StatsOverview> overview() {
        return Result.success(statsService.getOverview());
    }

    @GetMapping("/bp-trend")
    @ApiOperation("血压控制率趋势")
    public Result<List<TrendItem>> bpTrend() {
        return Result.success(statsService.getBpTrend());
    }

    @GetMapping("/glucose-trend")
    @ApiOperation("血糖控制率趋势")
    public Result<List<TrendItem>> glucoseTrend() {
        return Result.success(statsService.getGlucoseTrend());
    }

    @GetMapping("/doctor-comparison")
    @ApiOperation("医生对比数据")
    public Result<List<DoctorStats>> doctorComparison() {
        return Result.success(statsService.getDoctorComparison());
    }
}
