package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_follow_up_template")
public class FollowUpTemplate {
    @TableId(type = IdType.AUTO)
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
