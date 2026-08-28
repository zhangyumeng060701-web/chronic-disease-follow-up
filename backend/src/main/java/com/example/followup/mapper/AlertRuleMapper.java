package com.example.followup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.followup.entity.AlertRule;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlertRuleMapper extends BaseMapper<AlertRule> {

    @Select("SELECT * FROM t_alert_rule WHERE is_active = 1 ORDER BY threshold DESC")
    List<AlertRule> findActiveRules();
}
