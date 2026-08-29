/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.response.DoctorStats;
import com.example.followup.dto.response.StatsOverview;
import com.example.followup.dto.response.TrendItem;

import java.util.List;

/**
 * StatsService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface StatsService {

    /**
     * 查询getOverview。
     *
     * @return 返回值
     */
    StatsOverview getOverview();

    /**
     * 查询getBpTrend。
     *
     * @return 返回值
     */
    List<TrendItem> getBpTrend();

    /**
     * 查询getGlucoseTrend。
     *
     * @return 返回值
     */
    List<TrendItem> getGlucoseTrend();

    /**
     * 查询getDoctorComparison。
     *
     * @return 返回值
     */
    List<DoctorStats> getDoctorComparison();
}
