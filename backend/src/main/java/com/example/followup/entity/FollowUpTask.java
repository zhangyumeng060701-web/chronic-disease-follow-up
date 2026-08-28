package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_follow_up_task")
public class FollowUpTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private Long patientId;
    private String taskType;
    private String status;
    private Long ownerId;
    private String channel;
    private LocalDate dueDate;
    private LocalDateTime completedTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
