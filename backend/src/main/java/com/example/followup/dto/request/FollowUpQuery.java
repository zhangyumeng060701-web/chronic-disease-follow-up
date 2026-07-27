package com.example.followup.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class FollowUpQuery {

    private Long patientId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /** 逾期查询：筛选 next_follow_up_date 早于此日期的记录 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextFollowUpDateBefore;

    private long page = 1;
    private long size = 20;
}
