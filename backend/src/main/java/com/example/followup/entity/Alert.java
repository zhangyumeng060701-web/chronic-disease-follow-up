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
    private LocalDateTime resolveTime;
    private LocalDateTime createTime;
}
