package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String recipientType;
    private Long recipientId;
    private String channel;
    private String title;
    private String content;
    private String templateCode;
    private String status;
    private LocalDateTime readTime;
    private LocalDateTime createTime;
}
