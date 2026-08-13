package com.example.followup.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlertVO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String alertType;
    private String alertLevel;
    private String alertReason;
    private Integer isResolved;
    private LocalDateTime resolveTime;
    private LocalDateTime createTime;
}
