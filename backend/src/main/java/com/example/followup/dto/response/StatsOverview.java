package com.example.followup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsOverview {
    private Long totalPatients;
    private Integer monthlyCompleted;
    private Integer monthlyExpected;
    private String completionRate;
    private Long highRiskCount;
    private Long lostFollowUpCount;
}
