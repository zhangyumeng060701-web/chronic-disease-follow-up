package com.example.followup.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FollowUpTaskVO {
    private Long id;
    private Long planId;
    private Long patientId;
    private String patientName;
    private String taskType;
    private String status;
    private Long ownerId;
    private String ownerName;
    private String channel;
    private LocalDate dueDate;
    private LocalDateTime completedTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
