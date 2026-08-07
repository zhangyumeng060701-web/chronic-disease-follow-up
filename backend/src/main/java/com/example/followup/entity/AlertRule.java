package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_alert_rule")
public class AlertRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ruleName;
    private String indicator;
    private String operator;
    private BigDecimal threshold;
    private String alertLevel;
    private Integer isActive;
    private LocalDateTime createTime;
}
