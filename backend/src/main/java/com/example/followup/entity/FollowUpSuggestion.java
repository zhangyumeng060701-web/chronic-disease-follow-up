package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_follow_up_suggestion")
public class FollowUpSuggestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private Long followUpId;
    private String content;
    private BigDecimal confidence;
    private String evidence;
    private String riskLevel;
    private String source;
    private String status;
    private Long doctorId;
    private LocalDateTime confirmTime;
    private LocalDateTime createTime;
}
