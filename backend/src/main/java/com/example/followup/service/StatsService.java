package com.example.followup.service;

import com.example.followup.dto.response.DoctorStats;
import com.example.followup.dto.response.StatsOverview;
import com.example.followup.dto.response.TrendItem;
import java.util.List;

public interface StatsService {
    StatsOverview getOverview();
    List<TrendItem> getBpTrend();
    List<TrendItem> getGlucoseTrend();
    List<DoctorStats> getDoctorComparison();
}
