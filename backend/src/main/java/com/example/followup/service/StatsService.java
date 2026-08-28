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
    StatsOverview getOverview();
    List<TrendItem> getBpTrend();
    List<TrendItem> getGlucoseTrend();
    List<DoctorStats> getDoctorComparison();
}
