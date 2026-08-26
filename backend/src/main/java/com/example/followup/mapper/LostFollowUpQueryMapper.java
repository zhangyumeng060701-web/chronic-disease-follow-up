package com.example.followup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.LostFollowUpAlertRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LostFollowUpQueryMapper extends BaseMapper<LostFollowUpAlertRecord> {
    @Select("SELECT f.* FROM t_follow_up f " +
            "JOIN t_patient p ON p.id = f.patient_id AND p.status = 1 " +
            "WHERE f.id = (SELECT f2.id FROM t_follow_up f2 " +
            "WHERE f2.patient_id = f.patient_id " +
            "ORDER BY f2.follow_up_date DESC, f2.id DESC LIMIT 1) " +
            "AND f.next_follow_up_date IS NOT NULL " +
            "AND f.next_follow_up_date <= #{cutoffDate}")
    List<FollowUp> findLatestDueFollowUps(@Param("cutoffDate") LocalDate cutoffDate);
}
