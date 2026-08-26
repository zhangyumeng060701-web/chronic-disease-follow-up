package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_alert")
public class Alert {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private String alertType;
    private String alertLevel;
    private String alertReason;
    private Integer isResolved;
    private String alertStatus;
    private LocalDateTime contactTime;
    private String referralReason;
    private String recommendedActions;
    private String recheckItems;
    private String referralConditions;
    private String evidenceSource;
    private String riskLevel;
    private LocalDateTime resolveTime;
    private LocalDateTime createTime;
}
