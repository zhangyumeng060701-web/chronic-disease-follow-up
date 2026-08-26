package com.example.followup.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowUpTemplateVO {
    private Long id;
    private String templateCode;
    private String templateName;
    private String riskLevel;
    private Integer frequencyDays;
    private String followUpType;
    private String defaultContent;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
