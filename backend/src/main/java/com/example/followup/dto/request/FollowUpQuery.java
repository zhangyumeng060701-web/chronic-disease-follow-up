package com.example.followup.dto.request;

import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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

    @Min(value = 1, message = "页码不能小于1")
    private long page = 1;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private long size = 20;
}
