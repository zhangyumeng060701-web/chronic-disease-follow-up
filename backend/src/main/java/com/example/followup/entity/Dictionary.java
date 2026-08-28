package com.example.followup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_dictionary")
public class Dictionary {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String dictType;
    private String dictCode;
    private String dictLabel;
    private Integer sortNo;
    private Integer isActive;
    private LocalDateTime createTime;
}
