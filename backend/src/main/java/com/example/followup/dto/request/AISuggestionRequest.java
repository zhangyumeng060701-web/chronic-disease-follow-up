package com.example.followup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AISuggestionRequest {
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    private List<FollowUpInput> recentFollowUps;
    private String riskLevel;
}
