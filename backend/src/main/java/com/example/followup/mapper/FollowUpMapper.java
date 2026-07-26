package com.example.followup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.followup.entity.FollowUp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface FollowUpMapper extends BaseMapper<FollowUp> {

    @Select("SELECT DISTINCT f.patient_id FROM t_follow_up f " +
            "JOIN t_patient p ON f.patient_id = p.id " +
            "WHERE p.status = 1 AND f.next_follow_up_date < CURDATE() " +
            "AND f.patient_id NOT IN (SELECT patient_id FROM t_follow_up WHERE follow_up_date > f.next_follow_up_date)")
    List<Long> findOverduePatientIds();

    @Select("SELECT COUNT(*) FROM t_follow_up WHERE follow_up_date >= DATE_FORMAT(CURDATE(),'%Y-%m-01')")
    Integer countMonthlyCompleted();
}
