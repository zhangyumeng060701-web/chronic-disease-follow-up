/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.followup.entity.Alert;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AlertMapper 数据访问接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Mapper
public interface AlertMapper extends BaseMapper<Alert> {
    @Select("SELECT COUNT(*) FROM t_alert WHERE is_resolved = 0 AND alert_level = 'RED'")
    Long countHighRisk();

    @Select("SELECT COUNT(*) FROM t_alert WHERE is_resolved = 0 AND alert_type = 'LOST_FOLLOW_UP'")
    Long countLostFollowUp();

    @Insert("<script>" +
            "INSERT INTO t_alert (patient_id, alert_type, alert_level, alert_reason, is_resolved) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.patientId}, #{item.alertType}, #{item.alertLevel}, #{item.alertReason}, #{item.isResolved})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<Alert> alerts);
}
