package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_questionnaire")
public class Questionnaire {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String title;
    private String description;
    private String content;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
