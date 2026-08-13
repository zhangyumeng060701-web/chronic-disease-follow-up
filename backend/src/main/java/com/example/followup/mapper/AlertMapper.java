package com.example.followup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.followup.entity.Alert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AlertMapper extends BaseMapper<Alert> {

    @Select("SELECT COUNT(*) FROM t_alert WHERE is_resolved = 0 AND alert_level = 'RED'")
    Long countHighRisk();

    @Select("SELECT COUNT(*) FROM t_alert WHERE is_resolved = 0 AND alert_type = 'LOST_FOLLOW_UP'")
    Long countLostFollowUp();
}
