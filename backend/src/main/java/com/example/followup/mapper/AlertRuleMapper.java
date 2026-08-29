/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.followup.entity.AlertRule;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AlertRuleMapper 数据访问接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Mapper
public interface AlertRuleMapper extends BaseMapper<AlertRule> {
    @Select("SELECT * FROM t_alert_rule WHERE is_active = 1 ORDER BY threshold DESC")

    /**
     * 查询findActiveRules。
     *
     * @return 返回值
     */
    List<AlertRule> findActiveRules();
}
